package com.web.product.controller;

import com.web.product.service.impl.ProductServiceImpl;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.web.product.dto.ProductDto;
import com.web.product.model.Product;

import javax.validation.Valid;
import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping(path = "api/v1/products")
public class ProductController {
	
	private final ProductServiceImpl productService;

	@PostMapping
	public ResponseEntity<HttpStatus> createProduct(@Valid @RequestBody ProductDto productDto) {
		productService.createProduct(productDto);
		return ResponseEntity.ok(HttpStatus.CREATED);
	}

    @GetMapping
    public ResponseEntity<List<Product>> getProducts(Pageable pageable) {
        List<Product> getProducts = productService.getProducts(pageable);
        if (getProducts != null) {
            return new ResponseEntity<>(getProducts, HttpStatus.OK);
        }
        return null;
    }

	@GetMapping("/{product_code}")
	public ResponseEntity<Product> getProduct(@PathVariable("product_code") String productCode) {
		return ResponseEntity.ok(productService.getProduct(productCode));
	}

	@GetMapping("/exist/{product_code}")
	public boolean checkProductExists(@PathVariable("product_code") String productCode) {
		boolean checkProductExists = productService.checkProductExists(productCode);
		return checkProductExists;
	}

	@PutMapping("/{product_code}")
	public ResponseEntity<HttpStatus> updateProduct(@PathVariable("product_code") String productCode,@RequestBody Product product) {
		boolean updatedProduct = productService.updateProduct(productCode, product);
		if (updatedProduct) {
			return ResponseEntity.ok(HttpStatus.CREATED);
		}
		return null;
	}

	@DeleteMapping("/{product_code}")
	public ResponseEntity<HttpStatus> deleteProduct(@PathVariable("product_code") String productCode) {
		boolean updatedProduct = productService.deleteProduct(productCode);
		if (updatedProduct) {
			return ResponseEntity.ok(HttpStatus.CREATED);
		}
		return null;
	}

}
