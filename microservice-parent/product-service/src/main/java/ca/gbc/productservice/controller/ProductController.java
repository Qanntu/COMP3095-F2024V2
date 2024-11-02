package ca.gbc.productservice.controller;

import ca.gbc.productservice.dto.ProductRequest;
import ca.gbc.productservice.dto.ProductResponse;
import ca.gbc.productservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor

public class ProductController {

    private final ProductService productService;

    @PostMapping//Maps HTTP POST request to these methods. Typically, for creating new resources
    @ResponseStatus(HttpStatus.CREATED) //Sets the response status to 201 Created when a product is successfully created
    public ResponseEntity<ProductResponse> createProduct(@RequestBody ProductRequest productRequest){

        //The @RequestBody annotation indicates that the request body contains a ProductRequest object.
        ProductResponse createProduct = productService.createProduct(productRequest);

        //Set the headers (e.g., Location header if you want to indicate the URL of the created resource)
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", "/api/product" + createProduct.id());

        //Return the ResponseEntity with the 201 Created status, response body, and headers.
        return ResponseEntity
                .status(HttpStatus.CREATED) // set status to 201
                .headers(headers)
                .contentType(MediaType.APPLICATION_JSON) //set content type to JSON
                .body(createProduct); //return the created product in the response body
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ProductResponse> getAllProducts(){

        return productService.getAllProducts();
    }
    //Http://localhost:8090/api/product/

    @PutMapping("/{productId}")
    //@ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<?> updateProduct(@PathVariable("productId") String productId,
                                           @RequestBody ProductRequest productRequest) {
        String updateProductId = productService.updateProduct(productId,productRequest);

        // set the location header attribute
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", "api/product/" + updateProductId);
        return  new ResponseEntity<>(headers,HttpStatus.NO_CONTENT);
    }

    //http://localhost:8080/api/product/
    @DeleteMapping("/{productId}")
    public ResponseEntity<?> deleteProduct(@PathVariable("productId") String productId){
        productService.deleteProduct(productId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}



