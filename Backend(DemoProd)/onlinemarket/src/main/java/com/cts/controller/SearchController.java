//package com.cts.controller;
//
//import java.util.List;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.CrossOrigin;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.cts.dto.ProductDTO;
//import com.cts.service.SearchService;
//
//@RestController
//@CrossOrigin(origins = "http://127.0.0.1:3000")
//@RequestMapping("/OMP")
//public class SearchController {
//	
//	@Autowired
//	SearchService searchService;
//	
//	@GetMapping("/searchProductByName")
//	public ResponseEntity<List<ProductDTO>> searchProduct(@RequestParam String productName){
//		List<ProductDTO> products = searchService.searchProductByName(productName);
//		return ResponseEntity.ok(products);
//	}
//	
//	@GetMapping("/searchProductBySubsCount")
//	public ResponseEntity<List<ProductDTO>> searchProduct(@RequestParam int count){
//		List<ProductDTO> products = searchService.searchProductBySubsCount(count);
//		return ResponseEntity.ok(products);
//	}
//	
//	@GetMapping("/searchProductByRating")
//	public ResponseEntity<List<ProductDTO>> searchProduct(@RequestParam double rating){
//		List<ProductDTO> products = searchService.searchProductByRating(rating);
//		return ResponseEntity.ok(products);
//	}
//	
//	@GetMapping("/searchProductBySubsCountAndRating")
//	public ResponseEntity<List<ProductDTO>> searchProduct(@RequestParam int count, @RequestParam double rating){
//		List<ProductDTO> products = searchService.searchProductBySubsCountAndRating(count, rating);
//		return ResponseEntity.ok(products);
//	}
//	
//	@GetMapping("/searchProductByNameSubsRating")
//	public ResponseEntity<List<ProductDTO>> searchProduct(@RequestParam String name, @RequestParam int count, @RequestParam double rating){
//		List<ProductDTO> products = searchService.searchProductByNameSubsRating(name, count, rating);
//		return ResponseEntity.ok(products);
//	}
//	
//}

package com.cts.controller;
 
import java.util.List;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
 
import com.cts.dto.ProductViewDTO;
import com.cts.service.SearchService;
 
@RestController
@CrossOrigin(origins = "http://127.0.0.1:3000")
@RequestMapping("/OMP")
public class SearchController {
	@Autowired
	SearchService searchService;
	
	@GetMapping("/searchProductByName")
	public ResponseEntity<List<ProductViewDTO>> searchProduct(@RequestParam String productName){
		List<ProductViewDTO> products = searchService.searchProductByName(productName);
		return ResponseEntity.ok(products);
	}
	@GetMapping("/searchProductBySubsCount")
	public ResponseEntity<List<ProductViewDTO>> searchProduct(@RequestParam int count){
		List<ProductViewDTO> products = searchService.searchProductBySubsCount(count);
		return ResponseEntity.ok(products);
	}
	@GetMapping("/searchProductByRating")
	public ResponseEntity<?> searchProduct(@RequestParam double rating){
		List<ProductViewDTO> products = searchService.searchProductByRating(rating);
		return ResponseEntity.ok(products);
	}
	@GetMapping("/searchProductBySubsCountAndRating")
	public ResponseEntity<?> searchProduct(@RequestParam int count, @RequestParam double rating){
		List<ProductViewDTO> products = searchService.searchProductBySubsCountAndRating(count, rating);
		return ResponseEntity.ok(products);
	}
	@GetMapping("/searchProductByNameSubsRating")
	public ResponseEntity<?> searchProduct(@RequestParam String name, @RequestParam int count, @RequestParam double rating){
		List<ProductViewDTO> products = searchService.searchProductByNameSubsRating(name, count, rating);
		return ResponseEntity.ok(products);
	}
	@GetMapping("/searchProductByNameAndRating")
	public ResponseEntity<?> searchProduct(@RequestParam String name, @RequestParam double rating){
		List<ProductViewDTO> products = searchService.searchProductByNameAndRating(name, rating);
		return ResponseEntity.ok(products);
	}
	@GetMapping("/searchProductByNameAndSubsCount")
	public ResponseEntity<?> searchProduct(@RequestParam String name, @RequestParam int count){
		List<ProductViewDTO> products = searchService.searchProductByNameAndSubsCount(name, count);
		return ResponseEntity.ok(products);
	}
}
