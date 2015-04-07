package com.ctrip.infosec.flowtable4j.translate.entity;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by lpxie on 15-3-31.
 */
public class InfoSecurity_UserInfo
{
    private long _reqID = Long.MIN_VALUE;
    private String _uid = "";
    private String _userPassword = "";
    private Date _signUpDate = Calendar.getInstance().getTime();
    private String _cusCharacter = "";
    private int _experience = Integer.MIN_VALUE;
    private byte _vipGrade = Byte.MIN_VALUE;
    private String _isTempUser = "";
    private BigDecimal _totalPenalty = BigDecimal.ZERO;
    private String _isUidHasBlackCard = "";
    private Date _dataChange_LastTime = Calendar.getInstance().getTime();
    private String _bindedMobilePhone = "";
    private String _bindedEmail = "";
    private String _relatedMobilephone = "";
    private String _relatedEMail = "";
    private String _isBindedMobilePhone = "";
    private String _isBindedEmail = "";

    public long get_reqID()
    {
        return _reqID;
    }

    public void set_reqID(long _reqID)
    {
        this._reqID = _reqID;
    }

    public String get_uid()
    {
        return _uid;
    }

    public void set_uid(String _uid)
    {
        this._uid = _uid;
    }

    public String get_userPassword()
    {
        return _userPassword;
    }

    public void set_userPassword(String _userPassword)
    {
        this._userPassword = _userPassword;
    }

    public Date get_signUpDate()
    {
        return _signUpDate;
    }

    public void set_signUpDate(Date _signUpDate)
    {
        this._signUpDate = _signUpDate;
    }

    public String get_cusCharacter()
    {
        return _cusCharacter;
    }

    public void set_cusCharacter(String _cusCharacter)
    {
        this._cusCharacter = _cusCharacter;
    }

    public int get_experience()
    {
        return _experience;
    }

    public void set_experience(int _experience)
    {
        this._experience = _experience;
    }

    public byte get_vipGrade()
    {
        return _vipGrade;
    }

    public void set_vipGrade(byte _vipGrade)
    {
        this._vipGrade = _vipGrade;
    }

    public String get_isTempUser()
    {
        return _isTempUser;
    }

    public void set_isTempUser(String _isTempUser)
    {
        this._isTempUser = _isTempUser;
    }

    public BigDecimal get_totalPenalty()
    {
        return _totalPenalty;
    }

    public void set_totalPenalty(BigDecimal _totalPenalty)
    {
        this._totalPenalty = _totalPenalty;
    }

    public String get_isUidHasBlackCard()
    {
        return _isUidHasBlackCard;
    }

    public void set_isUidHasBlackCard(String _isUidHasBlackCard)
    {
        this._isUidHasBlackCard = _isUidHasBlackCard;
    }

    public Date get_dataChange_LastTime()
    {
        return _dataChange_LastTime;
    }

    public void set_dataChange_LastTime(Date _dataChange_LastTime)
    {
        this._dataChange_LastTime = _dataChange_LastTime;
    }

    public String get_bindedMobilePhone()
    {
        return _bindedMobilePhone;
    }

    public void set_bindedMobilePhone(String _bindedMobilePhone)
    {
        this._bindedMobilePhone = _bindedMobilePhone;
    }

    public String get_bindedEmail()
    {
        return _bindedEmail;
    }

    public void set_bindedEmail(String _bindedEmail)
    {
        this._bindedEmail = _bindedEmail;
    }

    public String get_relatedMobilephone()
    {
        return _relatedMobilephone;
    }

    public void set_relatedMobilephone(String _relatedMobilephone)
    {
        this._relatedMobilephone = _relatedMobilephone;
    }

    public String get_relatedEMail()
    {
        return _relatedEMail;
    }

    public void set_relatedEMail(String _relatedEMail)
    {
        this._relatedEMail = _relatedEMail;
    }

    public String get_isBindedMobilePhone()
    {
        return _isBindedMobilePhone;
    }

    public void set_isBindedMobilePhone(String _isBindedMobilePhone)
    {
        this._isBindedMobilePhone = _isBindedMobilePhone;
    }

    public String get_isBindedEmail()
    {
        return _isBindedEmail;
    }

    public void set_isBindedEmail(String _isBindedEmail)
    {
        this._isBindedEmail = _isBindedEmail;
    }
}
