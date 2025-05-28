import pandas as pd
import numpy as np
import re
from datetime import datetime


def clean_movie_data(input_file='raw-data.xlsx', output_file='processed-data.xlsx'):
    """
    输出处理：
    1. 剔除不规范的电影名（空白、空格、或者为"1"）
    2. 去除上映时间中的" 00:00:00"部分
    3. 保存到新的Excel文件
    """

    try:
        # 读取Excel文件
        print("正在读取数据...")
        df = pd.read_excel(input_file)

        print(f"原始数据行数: {len(df)}")
        print(f"列名: {df.columns.tolist()}")
        print("\n原始数据样例:")
        print(df.head())

        # 清理电影名字段
        print("\n开始清理电影名...")

        # 创建一个布尔索引来标识需要保留的行
        valid_rows = pd.Series([True] * len(df))

        # 检查名字字段是否存在
        if '名字' in df.columns:
            # 转换为字符串类型并去除前后空格
            df['名字'] = df['名字'].astype(str).str.strip()

            # 标识不规范的数据
            invalid_names = (
                    df['名字'].isna() |  # 空值
                    (df['名字'] == '') |  # 空字符串
                    (df['名字'] == 'nan') |  # 字符串"nan"
                    (df['名字'] == '1') |  # 字符串"1"
                    (df['名字'].str.len() == 0) |  # 长度为0
                    (df['名字'].str.match(r'^\s*$'))  # 只包含空格
            )

            valid_rows = valid_rows & ~invalid_names

            print(f"发现 {invalid_names.sum()} 行不规范的电影名")
            if invalid_names.sum() > 0:
                print("不规范的电影名示例:")
                print(df[invalid_names]['名字'].value_counts().head())

        # 清理上映时间字段
        print("\n开始处理上映时间...")

        if '上映时间' in df.columns:
            # 转换为字符串类型
            df['上映时间'] = df['上映时间'].astype(str)

            # 去除" 00:00:00"部分
            df['上映时间'] = df['上映时间'].str.replace(' 00:00:00', '', regex=False)

            # 也处理可能的其他时间格式
            df['上映时间'] = df['上映时间'].str.replace(r'\s+\d{2}:\d{2}:\d{2}', '', regex=True)

            print("上映时间处理完成")
            print("处理后的上映时间样例:")
            print(df['上映时间'].head())

        # 应用过滤条件
        df_cleaned = df[valid_rows].copy()

        # 重置索引
        df_cleaned = df_cleaned.reset_index(drop=True)

        print(f"\n清理后数据行数: {len(df_cleaned)}")
        print(f"删除了 {len(df) - len(df_cleaned)} 行不规范数据")

        # 显示清理后的数据概览
        print("\n清理后的数据样例:")
        print(df_cleaned.head())

        # 保存到新的Excel文件
        print(f"\n正在保存数据到 {output_file}...")
        df_cleaned.to_excel(output_file, index=False)

        print("数据处理完成！")

        # 显示数据统计信息
        print("\n=== 数据统计信息 ===")
        print(f"原始数据: {len(df)} 行")
        print(f"清理后数据: {len(df_cleaned)} 行")
        print(f"删除比例: {((len(df) - len(df_cleaned)) / len(df) * 100):.2f}%")

        if '评分' in df_cleaned.columns:
            print(f"平均评分: {df_cleaned['评分'].mean():.2f}")
            print(f"最高评分: {df_cleaned['评分'].max()}")
            print(f"最低评分: {df_cleaned['评分'].min()}")

        if '年代' in df_cleaned.columns:
            print(f"年代范围: {df_cleaned['年代'].min()} - {df_cleaned['年代'].max()}")

        return df_cleaned

    except Exception as e:
        print(f"处理过程中出现错误: {str(e)}")
        return None


def validate_data(df):
    """
    验证清理后的数据质量
    """
    if df is None:
        return

    print("\n=== 数据质量检查 ===")

    # 检查是否还有空的电影名
    if '名字' in df.columns:
        empty_names = df['名字'].isna().sum() + (df['名字'] == '').sum()
        print(f"空电影名数量: {empty_names}")

    # 检查上映时间格式
    if '上映时间' in df.columns:
        time_with_seconds = df['上映时间'].str.contains('00:00:00', na=False).sum()
        print(f"仍包含时间部分的条目: {time_with_seconds}")

    # 检查各字段的缺失值情况
    print("\n各字段缺失值统计:")
    missing_stats = df.isnull().sum()
    for col, missing_count in missing_stats.items():
        if missing_count > 0:
            print(f"{col}: {missing_count} ({missing_count / len(df) * 100:.2f}%)")


if __name__ == "__main__":
    # 执行数据清理
    cleaned_data = clean_movie_data()

    # 验证数据质量
    if cleaned_data is not None:
        validate_data(cleaned_data)