package com.funny.controller;

import com.funny.entity.OAuth;
import com.funny.entity.User;
import com.funny.repository.OAuthRepository;
import com.funny.repository.UserRepository;
import com.funny.service.UserService;
import com.funny.service.auth.OAuthServiceDeractor;
import com.funny.service.auth.OAuthServices;
import com.funny.vo.ErrorInfo;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Controller
@RequestMapping("user")
public class UserController {


    @Autowired
    private OAuthServices oAuthServices;
    @Autowired
    private OAuthRepository oAuthRepository;
    @Autowired
    private UserRepository userRepository;


    @RequestMapping(value = {"", "/login"}, method = RequestMethod.GET)
    public String showLogin(Model model) {
        model.addAttribute("oAuthServices", oAuthServices.getAllOAuthServices());
        return "auth/index";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public ErrorInfo<String> login(User user) {
        ErrorInfo<String> errorInfo = new ErrorInfo<>();
        try {
            if (user == null) {
                throw new Exception("user不能为空");
            }

            if (user.getOAuthId() != null) {
                user = userRepository.findByOAuthTypeAndOAuthId(user.getOAuthType(), user.getOAuthId());
                if (user == null) {
                    throw new Exception("第三方账号未绑定");
                }
            } else {
                String password = user.getPassword();
                user = userRepository.findByMobileOrEmail(user.getMobile(), user.getEmail());
                if (user == null) {
                    throw new Exception("邮箱或者手机错误");
                }
                if (!password.equals(user.getPassword())) {
                    throw new Exception("密码错误");
                }
            }
            errorInfo.setStatus(true);
            errorInfo.setMsg("登陆成功");
            errorInfo.setData(user.getId() + "");
        } catch (Exception e) {
            errorInfo.setMsg("登陆失败，" + e.getMessage());
        }
        return errorInfo;
    }

    @RequestMapping(value = "/{type}/callback", method = RequestMethod.GET)
    public String claaback(@PathVariable OAuth.Type type, String code, HttpServletRequest request, Model model) {
        OAuthServiceDeractor oAuthService = oAuthServices.getOAuthService(type);
        Token accessToken = oAuthService.getAccessToken(null, new Verifier(code));
        OAuth oAuthInfo = oAuthService.getOAuthUser(accessToken);
        OAuth oAuthUser = oAuthRepository.findByTypeAndOAuthId(oAuthInfo.getType(), oAuthInfo.getOAuthId());
        if (oAuthUser == null) {
            System.out.println("auth/register");
            model.addAttribute("oAuthInfo", oAuthInfo);
            return "auth/register";
        }
        request.getSession().setAttribute("oauthUser", oAuthUser);
        System.out.println("/index");
        return "redirect:/index";
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ResponseBody
    public ErrorInfo<String> register(@Valid User user, BindingResult bindingResult) {
        ErrorInfo<String> errorInfo = new ErrorInfo<>();
        try {
            // 用户信息合法验证
            if (bindingResult.hasErrors()) {
                throw new Exception(bindingResult.getFieldError().getDefaultMessage());
            }

            // 如果已经存在用户，则注册失败
            if (userRepository.findByMobileOrEmail(user.getMobile(), user.getEmail()) != null) {
                throw new Exception("邮箱或手机已经被占用");
            }

            userRepository.save(user);
            errorInfo.setStatus(true);
            errorInfo.setMsg("注册新用户成功");
            errorInfo.setData(user.getId() + "");
        } catch (Exception e) {
            errorInfo.setMsg("注册新用户失败，" + e.getMessage());
        }

        return errorInfo;
    }

    @RequestMapping(value = "/success", method = RequestMethod.GET)
    @ResponseBody
    public Object success(HttpServletRequest request) {
        return request.getSession().getAttribute("oauthUser");
    }


}
