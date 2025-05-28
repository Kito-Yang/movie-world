import pandas as pd
import pymysql
from sqlalchemy import create_engine
import warnings

warnings.filterwarnings('ignore')


def excel_to_mysql():
    # MySQL连接配置
    config = {
        'host': 'localhost',
        'port': 3306,
        'user': 'root',
        'password': '123456',
        'charset': 'utf8mb4'
    }

    try:
        # 读取Excel文件
        print("正在读取Excel文件...")
        df = pd.read_excel('processed-data.xlsx')
        print(f"读取到 {len(df)} 条记录")

        # 连接MySQL并创建数据库
        connection = pymysql.connect(**config)
        cursor = connection.cursor()

        # 创建数据库（如果不存在）
        cursor.execute("CREATE DATABASE IF NOT EXISTS movie CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
        cursor.execute("USE movie")

        # 创建movies表
        create_table_sql = '''
        CREATE TABLE IF NOT EXISTS movies (
            id INT AUTO_INCREMENT PRIMARY KEY,
            序号 INT,
            名字 VARCHAR(255),
            投票人数 INT,
            类型 VARCHAR(100),
            产地 VARCHAR(100),
            上映时间 DATETIME,
            时长 INT,
            年代 INT,
            评分 DECIMAL(3,1),
            首映地点 VARCHAR(255)
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
        '''

        cursor.execute(create_table_sql)
        print("数据表创建成功")

        # 清空表（如果需要重新导入数据）
        cursor.execute('DELETE FROM movies')
        connection.commit()

        # 关闭pymysql连接，使用sqlalchemy进行批量插入
        cursor.close()
        connection.close()

        # 使用sqlalchemy创建引擎进行批量插入
        engine = create_engine(
            f"mysql+pymysql://{config['user']}:{config['password']}@{config['host']}:{config['port']}/movie?charset=utf8mb4")

        # 数据预处理 - 处理可能的问题数据
        print("正在预处理数据...")

        # 查看数据基本信息
        print(f"数据形状: {df.shape}")
        print(f"列名: {list(df.columns)}")
        print("\n数据类型:")
        print(df.dtypes)

        # 处理可能的NaN值
        df = df.fillna('')

        # 处理时间字段 - 如果转换失败则保持原格式
        if '上映时间' in df.columns:
            try:
                df['上映时间'] = pd.to_datetime(df['上映时间'], errors='coerce')
                print("时间字段转换成功")
            except Exception as e:
                print(f"时间字段转换失败，保持原格式: {e}")

        # 显示前几行数据用于调试
        print("\n前3行数据:")
        print(df.head(3))

        # 将DataFrame数据插入数据库
        print("\n正在插入数据到MySQL...")
        try:
            df.to_sql('movies', engine, if_exists='append', index=False, method='multi', chunksize=1000)
            print("数据插入完成")
        except Exception as insert_error:
            print(f"批量插入失败: {insert_error}")
            print("尝试逐行插入...")

            # 如果批量插入失败，尝试逐行插入
            connection = pymysql.connect(**config)
            cursor = connection.cursor()
            cursor.execute("USE movie")

            success_count = 0
            error_count = 0

            for index, row in df.iterrows():
                try:
                    insert_sql = '''
                    INSERT INTO movies (序号, 名字, 投票人数, 类型, 产地, 上映时间, 时长, 年代, 评分, 首映地点)
                    VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
                    '''
                    values = tuple(row)
                    cursor.execute(insert_sql, values)
                    success_count += 1
                except Exception as row_error:
                    error_count += 1
                    print(f"第 {index + 1} 行插入失败: {row_error}")
                    print(f"问题数据: {row.to_dict()}")

            connection.commit()
            cursor.close()
            connection.close()
            print(f"逐行插入完成: 成功 {success_count} 条, 失败 {error_count} 条")

        # 重新连接验证数据
        connection = pymysql.connect(**config)
        cursor = connection.cursor()
        cursor.execute("USE movie")

        # 验证数据是否成功插入
        cursor.execute('SELECT COUNT(*) FROM movies')
        count = cursor.fetchone()[0]
        print(f"成功插入 {count} 条记录到MySQL数据库")

        # 显示前5条记录作为验证
        cursor.execute('SELECT * FROM movies LIMIT 5')
        rows = cursor.fetchall()
        print("\n前5条记录：")
        for row in rows:
            print(row)

        # 显示表结构
        cursor.execute('DESCRIBE movies')
        columns = cursor.fetchall()
        print("\n表结构：")
        for col in columns:
            print(f"列名: {col[0]}, 类型: {col[1]}")

    except Exception as e:
        print(f"操作失败: {str(e)}")
        print("请检查：")
        print("1. MySQL服务是否启动")
        print("2. 用户名和密码是否正确")
        print("3. 是否安装了 pymysql: pip install pymysql")
        print("4. 是否安装了 sqlalchemy: pip install sqlalchemy")

    finally:
        # 关闭数据库连接
        if 'cursor' in locals() and cursor:
            cursor.close()
        if 'connection' in locals() and connection:
            connection.close()
            print("\nMySQL连接已关闭")


if __name__ == "__main__":
    excel_to_mysql()