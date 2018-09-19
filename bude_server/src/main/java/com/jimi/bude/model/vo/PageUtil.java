package com.jimi.bude.model.vo;

import java.util.List;

import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

public class PageUtil<T> {
	private Integer totallyPage;
	private Integer totallyData;
	private Integer currentPage;
	private Integer pageSize;
	private List<T> list;

	public Integer getCurrentPage() {
		return currentPage;
	}
	public void setCurrentPage(Integer currentPage) {
		this.currentPage = currentPage;
	}
	public Integer getPageSize() {
		return pageSize;
	}
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}
	public Integer getTotallyPage() {
		return totallyPage;
	}
	public void setTotallyPage(Integer totallyPage) {
		this.totallyPage = totallyPage;
	}
	public Integer getTotallyData() {
		return totallyData;
	}
	public void setTotallyData(Integer totallyData) {
		this.totallyData = totallyData;
	}
	public List<T> getList() {
		return list;
	}
	public void setList(List<T> list) {
		this.list = list;
	}
	
	public void fill(Page<Record> pageRecord, List<T> list){
		this.setCurrentPage(pageRecord.getPageNumber());
		this.setPageSize(pageRecord.getPageSize());
		this.setTotallyData(pageRecord.getTotalRow());
		this.setTotallyPage(pageRecord.getTotalPage());
		this.setList(list);
	}
}
