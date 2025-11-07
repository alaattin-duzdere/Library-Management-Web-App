package com.example.library_management.common.util;

import com.example.library_management.borrowing.dto.DtoBorrowResponse;
import com.example.library_management.borrowing.model.Borrowing;
import com.example.library_management.borrowing.repository.BorrowingRepository;
import com.example.library_management.exceptions.client.ResourceNotFoundException;
import com.example.library_management.penalties.model.Penalty;
import com.example.library_management.penalties.repository.PenaltyRepository;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Optional;

@Component
public class CustomPermissionEvaluator implements PermissionEvaluator {


    @Override
    public boolean hasPermission(Authentication auth, Serializable targetId, String targetType, Object permission) {
        return false;
    }
    /**
     * @PostAuthorize için ana metot. Dönen nesne üzerinden yetki kontrolü yapar.
     * Bu metot DTO'ları anlayacak şekilde yazılmıştır!
     */
    @Override
    public boolean hasPermission(Authentication auth, Object targetDomainObject, Object permission) {
        if (!hasAuth(auth)) return false;
        if (isAdmin(auth)) return true; // Admin her şeyi görebilir.

        Long currentUserId = getCurrentUserId(auth);

        if (targetDomainObject instanceof DtoBorrowResponse dto) {
            return dto.getUserId().equals(currentUserId);
        }
        return false;
    }

    /**
     * @PreAuthorize içinde doğrudan userId kontrolü için public yardımcı metot.
     * Örn: @PreAuthorize("hasRole('ADMIN') or @customPermissionEvaluator.isOwner(authentication, #userId)")
     */
    public boolean isOwner(Authentication auth, Long userId) {
        if (!hasAuth(auth)) return false;
        if (isAdmin(auth)) return true; // Admin ise zaten yetkilidir.

        return getCurrentUserId(auth).equals(userId);
    }

    private boolean hasAuth(Authentication auth) {
        return auth != null && auth.isAuthenticated();
    }

    private boolean isAdmin(Authentication auth) {
        return auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ADMIN"));
    }

    private Long getCurrentUserId(Authentication auth) {
        Object principal = auth.getPrincipal();
        return Long.parseLong(principal.toString());
    }
}