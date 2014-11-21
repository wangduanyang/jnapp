package com.dc.jnapp_v1;

public class MsgInfo {
	private String replyname;
	private String name;
	private String msg;
	public MsgInfo(){
		
	}
	public MsgInfo(String replyname, String name, String msg)  
    {  
        super();  
        this.setReplyname(replyname);
        this.setName(name);  
        this.setMsg(msg);
    }
	public String getReplyname() {
		return replyname;
	}
	public void setReplyname(String replyname) {
		this.replyname = replyname;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}  
}
