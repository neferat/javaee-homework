package boot.spring.mapper;

import boot.spring.po.Staff;
import boot.spring.po.User;

public interface LoginMapper {
	Staff getpwdbyname(String name);
	User getUserByUsername(String username);
}
