package beans;

public class Plant {
	private int pid;
	private String pname;
	private String pdesc;
	private String pics;
	private String phists;
	private String datatime;
	public String getDatatime() {
		return datatime;
	}

	public void setDatatime(String datatime) {
		this.datatime = datatime;
	}

	private double val;

	public Plant() {
		super();
	}

	public Plant(int pid, String pname, String pdesc, String pics, String phists) {
		super();
		this.pid = pid;
		this.pname = pname;
		this.pdesc = pdesc;
		this.pics = pics;
		this.phists = phists;
	}

	public int getPid() {
		return pid;
	}

	public void setPid(int pid) {
		this.pid = pid;
	}

	public String getPname() {
		return pname;
	}

	public void setPname(String pname) {
		this.pname = pname;
	}

	public String getPdesc() {
		return pdesc;
	}

	public void setPdesc(String pdesc) {
		this.pdesc = pdesc;
	}

	public String getPics() {
		return pics;
	}

	public void setPics(String pics) {
		this.pics = pics;
	}

	public String getPhists() {
		return phists;
	}

	public void setPhists(String phists) {
		this.phists = phists;
	}

	public double getVal() {
		return val;
	}

	public Plant setVal(double val) {
		this.val = val;
		return this;
	}

}
