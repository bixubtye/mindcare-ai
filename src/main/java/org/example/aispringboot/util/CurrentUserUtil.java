package org.example.aispringboot.util;

import org.example.aispringboot.exception.BusinessException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class CurrentUserUtil {
    private CurrentUserUtil() {
    }

    public static JwtTokenUtil.JwtToken getCurrentUser() {
        JwtTokenUtil.JwtToken currentUser = JwtTokenUtil.TokenVerificationResult(JwtTokenUtil.getCurrentToken());
        if (currentUser == null || !currentUser.getValid()) {
            throw new BusinessException("用户未登录");
        }
        return currentUser;
    }

    public static Long getUserId() {
        return getCurrentUser().getUserId();
    }

    public static boolean isAdmin() {
        return isAuthenticated() && Integer.valueOf(2).equals(getCurrentUser().getRoleType());
    }

    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);
    }
}
