package cn.eurasia.oj.services;

import cn.eurasia.oj.entities.User;
import cn.eurasia.oj.exceptions.BusinessException;
import cn.eurasia.oj.repositories.UserRepository;
import cn.eurasia.oj.utils.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

@Service
public class UserCenterService {
  @Autowired
  private UserRepository userRepository;

  public User getUser(Long userId) throws BusinessException {
    return userRepository.findById(userId).orElseThrow(() -> new BusinessException("找不到"));
  }

  public Map login(User userParam) throws BusinessException, UnsupportedEncodingException {
    Map result = new HashMap();
    User user = userRepository.findByUsername(userParam.getUsername());
    if (Objects.isNull(user) && !user.getPassword().equals(userParam.getPassword())) {
      throw new BusinessException("账号或密码错误");
    }
    JSONObject jsonObject = new JSONObject(user);
    String token = JwtUtil.build(jsonObject.toString());
    result.put("user", user);
    result.put("token", token);
    return result;
  }

  public Page<User> getUsersByPage(Pageable pageable) {
    return userRepository.findAll(pageable);
  }

  public User addUser(User user) throws BusinessException {
    if (Objects.nonNull(isExist(user))) {
      throw new BusinessException(String.valueOf(isExist(user)));
    }
    user.setPassword("123456");
    userRepository.save(user);
    return user;
  }

  private Object isExist(User user) {
    User currentUser = userRepository.findByUsernameOrPhoneOrEmail(user.getUsername(), user.getPhone(), user.getEmail());
    if (Objects.nonNull(currentUser)) {
      if (currentUser.getUsername().equals(user.getUsername())) return "用户名已存在";
      if (currentUser.getPhone().equals(user.getPhone())) return "电话号码已存在";
      if (currentUser.getEmail().equals(user.getEmail())) return "邮箱已存在";
    }
    return null;
  }

  public void putUser(User user) throws BusinessException {
    userRepository.findById(user.getId()).orElseThrow(
      () -> new BusinessException("无当前数据"));
    userRepository.save(user);
  }

  public Map getUserFromToken(Map token) throws Exception {
    Map<String, Object> body = JwtUtil.parseTokenToMap(token.get("token").toString());
    return JwtUtil.convertUser(body);
  }
}
