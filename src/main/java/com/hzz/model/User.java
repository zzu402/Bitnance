package com.hzz.model;

import com.hzz.common.dao.AbstractModel;
import com.hzz.common.dao.annotation.Column;
import com.hzz.common.dao.annotation.Table;

/**
 * @Author: huangzz
 * @Description:
 * @Date :2018/3/8
 */
@Table("bitcon_user")
public class User extends AbstractModel<User> {
    @Column(pk=true)
    private Long id;
    @Column
    private String secret_key;
    @Column
    private String api_key;
    @Column
    private String name;
    @Column
    private String email;
    @Column
    private String sellTemplet;
    @Column
    private String buyTemplet;
    @Column
    private String salt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSecret_key() {
        return secret_key;
    }

    public void setSecret_key(String secret_key) {
        this.secret_key = secret_key;
    }

    public String getApi_key() {
        return api_key;
    }

    public void setApi_key(String api_key) {
        this.api_key = api_key;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSellTemplet() {
        return sellTemplet;
    }

    public void setSellTemplet(String sellTemplet) {
        this.sellTemplet = sellTemplet;
    }

    public String getBuyTemplet() {
        return buyTemplet;
    }

    public void setBuyTemplet(String buyTemplet) {
        this.buyTemplet = buyTemplet;
    }
}
