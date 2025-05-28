package com.movie.backend.common;

import java.util.List;

public class PageResult<T> {
    private List<T> records;        // 当前页数据
    private long total;             // 总记录数
    private long size;              // 每页显示数量
    private long current;           // 当前页
    private long pages;             // 总页数

    public PageResult() {}

    public PageResult(List<T> records, long total, long size, long current, long pages) {
        this.records = records;
        this.total = total;
        this.size = size;
        this.current = current;
        this.pages = pages;
    }

    // Getter 和 Setter 方法
    public List<T> getRecords() { return records; }
    public void setRecords(List<T> records) { this.records = records; }

    public long getTotal() { return total; }
    public void setTotal(long total) { this.total = total; }

    public long getSize() { return size; }
    public void setSize(long size) { this.size = size; }

    public long getCurrent() { return current; }
    public void setCurrent(long current) { this.current = current; }

    public long getPages() { return pages; }
    public void setPages(long pages) { this.pages = pages; }

    @Override
    public String toString() {
        return "PageResult{" +
                "records=" + records +
                ", total=" + total +
                ", size=" + size +
                ", current=" + current +
                ", pages=" + pages +
                '}';
    }
}