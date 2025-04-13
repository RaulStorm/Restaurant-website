package org.example.restaurantwebsite.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.restaurantwebsite.security.JwtTokenProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        String token = resolveToken(request);
        if (token != null && jwtTokenProvider.validateToken(token)) {
            Authentication auth = jwtTokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}

//try {
//
//            File file = new File("image.jpg");
//
//            FileInputStream fis = new FileInputStream(file);
//
//            byte[] bytes = new byte[(int) file.length()];
//
//            fis.read(bytes);
//
//            fis.close();
//
//            String base64 = Base64.getEncoder().encodeToString(bytes);
//
//            System.out.println("Base64 код:\n" + base64);
//
//        } catch (Exception e) {
//
//            e.printStackTrace();
//
//        }
//
//try {
//
//            String base64 = "ТВОЙ_БАЗА64_КОД_ТУТ";
//
//            byte[] imageBytes = Base64.getDecoder().decode(base64);
//
//            FileOutputStream fos = new FileOutputStream("output.jpg");
//
//            fos.write(imageBytes);
//
//            fos.close();
//
//            System.out.println("Файл сохранён как output.jpg");
//
//        } catch (Exception e) {
//
//            e.printStackTrace();
//
//        }
//