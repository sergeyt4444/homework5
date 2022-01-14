package com.netcracker.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.netcracker.exception.ResourceNotFoundException;
import com.netcracker.model.Shop;
import com.netcracker.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/rest")
public class ShopController {

    @Autowired
    ShopService shopService;
    private ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("/shops")
    public List<Shop> getAllShops() {
        return shopService.findAll();
    }

    @GetMapping("/shops/SovSorm")
    public List<String> getByAddressContainingSormSov() {
        return shopService.findByAddressContainingSormSov();
    }

    @GetMapping("/shops/{id}")
    public ResponseEntity<Shop> getShopById(@PathVariable(value = "id")Integer id) throws ResourceNotFoundException {
        Shop shop = shopService.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Shop not found for id: " + id)
        );
        return ResponseEntity.ok(shop);
    }


    @DeleteMapping("/shops/{id}")
    public Map<String, Boolean> deleteShop(@PathVariable (value = "id")Integer id) throws ResourceNotFoundException {
        Shop shop = shopService.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Shop not found for id: " + id)
        );
        shopService.delete(shop);

        Map<String, Boolean> response = new HashMap<>();
        response.put("deleted", true);
        return response;
    }

    @PatchMapping(path = "/shops/{id}", consumes = "application/json-patch+json")
    public ResponseEntity<Shop> updateShop(@PathVariable Integer id, @RequestBody JsonPatch patch)
            throws ResourceNotFoundException{
        try {
            Shop shop = shopService.findById(id).orElseThrow(
                    () -> new ResourceNotFoundException("Shop not found for id: " + id));
            Shop shopPatched = applyPatchToShop(patch, shop);
            shopService.addShop(shopPatched);
            return ResponseEntity.ok(shopPatched);
        } catch (JsonPatchException | JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private Shop applyPatchToShop(
            JsonPatch patch, Shop targetShop) throws JsonPatchException, JsonProcessingException {
        JsonNode patched = patch.apply(objectMapper.convertValue(targetShop, JsonNode.class));
        return objectMapper.treeToValue(patched, Shop.class);
    }

    @PostMapping("/shops")
    public ResponseEntity<Shop> postShop(@RequestBody Shop shop) {
        Shop shopRes = this.shopService.addShop(shop);

        return new ResponseEntity<>(shopRes, HttpStatus.OK);
    }

    @PutMapping("/shops/{id}")
    Shop replaceShop(@RequestBody Shop shop, @PathVariable Integer id) {
        return shopService.findById(id)
                .map(shopRes -> {
                    shopRes.setAddress(shop.getAddress());
                    shopRes.setCommis(shop.getCommis());
                    return shopService.addShop(shopRes);
                })
                .orElseGet(() -> {
                    return shopService.addShop(shop);
                });
    }

}
