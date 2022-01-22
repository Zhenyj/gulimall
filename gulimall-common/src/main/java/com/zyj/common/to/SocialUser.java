package com.zyj.common.to;

import lombok.Data;

/**
 * @author lulx
 * @date 2022-01-21 20:54
 **/
@Data
public class SocialUser {
    private String access_token;
    private Long id;
    private String login;
    private String name;
    private String avatar_url;
    private String email;
}
