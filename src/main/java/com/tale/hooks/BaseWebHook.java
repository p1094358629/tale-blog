package com.tale.hooks;

import com.blade.ioc.annotation.Bean;
import com.blade.mvc.hook.Signature;
import com.blade.mvc.hook.WebHook;
import com.blade.mvc.http.Request;
import com.blade.mvc.http.Response;
import com.tale.init.TaleConst;
import com.tale.model.entity.Users;
import com.tale.utils.TaleUtils;
import lombok.extern.slf4j.Slf4j;

@Bean
@Slf4j
public class BaseWebHook implements WebHook {

    @Override
    public boolean before(Signature signature) {
        Request  request  = signature.request();
        Response response = signature.response();

        String uri = request.uri();
        String ip  = request.address();
        request.session().attribute("ip", ip);
        // 禁止该ip访问
        if (TaleConst.BLOCK_IPS.contains(ip)) {
            response.text("You have been banned, brother");
            return false;
        }

        log.info("UserAgent: {}", request.userAgent());
        log.info("用户访问地址: {}, 来路地址: {}", uri, ip);

        if (uri.startsWith(TaleConst.STATIC_URI)) {
            return true;
        }

        if (!TaleConst.INSTALLED && !uri.startsWith(TaleConst.INSTALL_URI)) {
            response.redirect(TaleConst.INSTALL_URI);
            return false;
        }
        //ip不同,则判断为异地登陆,暂不考虑用户,因为只有我一个
        if (!ip.equals(TaleConst.SINGLElOGIN.get("ip")) && TaleConst.SINGLElOGIN.get("ip")!=null) {
            //强制用户登出
            TaleUtils.logout(request.session(), response);
            System.err.println("用户登出");
//            request.session().removeAttribute(TaleConst.LOGIN_SESSION_KEY);
            TaleConst.SINGLElOGIN.put("ip", ip);
//            response.go("/admin/login");
        } 

        if (TaleConst.INSTALLED) {
            return isRedirect(request, response);
        }
        return true;
    }

    private boolean isRedirect(Request request, Response response) {
        Users  user = TaleUtils.getLoginUser();
        String uri  = request.uri();
        if (null == user) {
            Integer uid = TaleUtils.getCookieUid(request);
            if (null != uid) {
                user = new Users().find(uid);
                request.session().attribute(TaleConst.LOGIN_SESSION_KEY, user);
            }
        }
        if (uri.startsWith(TaleConst.ADMIN_URI) && !uri.startsWith(TaleConst.LOGIN_URI)) {
            if (null == user) {
                response.redirect(TaleConst.LOGIN_URI);
                return false;
            }
            request.attribute(TaleConst.PLUGINS_MENU_NAME, TaleConst.PLUGIN_MENUS);
        }
        return true;
    }

}
