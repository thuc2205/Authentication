package com.example.testauthor.controller;

import com.example.testauthor.Services.ServiceProduct;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ControllerRest {
    private final ServiceProduct serviceProduct;
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/product/delete/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        try {
            serviceProduct.deleteProduct(id);
            return ResponseEntity.ok("xóa thành công");
        }catch (Exception e){
            return ResponseEntity.badRequest().body(HttpStatus.NOT_FOUND);

        }

    }
}
