package payload;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Ticket {
	
	List<String> customItems;
	String employeeId;
	
	@JsonProperty("enum")
	String orderType;
	String ordertype;
	public String getOrdertype() {
		return ordertype;
	}
	public void setOrdertype(String ordertype) {
		this.ordertype = ordertype;
	}
	int noOfGuests;
	String note;
	String orderSource;
	public List<String> getCustomItems() {
		return customItems;
	}
	public void setCustomItems(List<String> customItems) {
		this.customItems = customItems;
	}
	String serveType;
	String status;
	int tableNumber;
	boolean taxExemption;
	List<String> ticketItems;
	String ticketPaymentStatus;
	int ticketId;
	String orderStatus;
	int page;
	int perPage;
	
	public int getPage() {
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}
	public int getPerPage() {
		return perPage;
	}
	public void setPerPage(int perPage) {
		this.perPage = perPage;
	}
	public String getOrderStatus() {
		return orderStatus;
	}
	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}
	public int getTicketId() {
		return ticketId;
	}
	public void setTicketId(int ticketId) {
		this.ticketId = ticketId;
	}
	public String getEmployeeId() {
		return employeeId;
	}
	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}
	public String getOrderType() {
		return orderType;
	}
	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}
	public int getNoOfGuests() {
		return noOfGuests;
	}
	public void setNoOfGuests(int noOfGuests) {
		this.noOfGuests = noOfGuests;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public String getOrderSource() {
		return orderSource;
	}
	public void setOrderSource(String orderSource) {
		this.orderSource = orderSource;
	}
	public String getServeType() {
		return serveType;
	}
	public void setServeType(String serveType) {
		this.serveType = serveType;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public int getTableNumber() {
		return tableNumber;
	}
	public void setTableNumber(int tableNumber) {
		this.tableNumber = tableNumber;
	}
	public boolean isTaxExemption() {
		return taxExemption;
	}
	public void setTaxExemption(boolean taxExemption) {
		this.taxExemption = taxExemption;
	}
	
	public List<String> getTicketItems() {
		return ticketItems;
	}
	public void setTicketItems(List<String> ticketItems) {
		this.ticketItems = ticketItems;
	}
	public String getTicketPaymentStatus() {
		return ticketPaymentStatus;
	}
	public void setTicketPaymentStatus(String ticketPaymentStatus) {
		this.ticketPaymentStatus = ticketPaymentStatus;
	}

}
