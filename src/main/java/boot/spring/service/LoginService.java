package boot.spring.service;

import boot.spring.po.User;

public interface LoginService {
	String getpwdbyname(String name);
	User getUserByUsername(String username);
}
