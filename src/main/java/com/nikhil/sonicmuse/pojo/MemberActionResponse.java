package com.nikhil.sonicmuse.pojo;

public class MemberActionResponse extends WebSocketBaseResponse
{
    MemberDTO member;

    public MemberDTO getMember()
    {
        return member;
    }

    public void setMember(MemberDTO member)
    {
        this.member = member;
    }
}
