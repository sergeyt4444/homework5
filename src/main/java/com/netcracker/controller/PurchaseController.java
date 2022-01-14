package com.netcracker.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.netcracker.exception.ResourceNotFoundException;
import com.netcracker.model.Purchase;
import com.netcracker.model.PurchaseCustomerNameAndShopName;
import com.netcracker.service.PurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/rest")
public class PurchaseController {

    @Autowired
    PurchaseService purchaseService;
    private ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("/purchases")
    public List<Purchase> getAllPurchases() {
        return purchaseService.findAll();
    }

    @GetMapping("/purchases/distinct_months")
    public Set<String> getAllDistinctMonths() {
        return purchaseService.findDistinctMonths();
    }

    @GetMapping("/purchases/names_only")
    public List<PurchaseCustomerNameAndShopName> getAllCustomerNamesAndShopNames() {
        return purchaseService.findAllCustomerNamesAndShopNames();
    }

    @GetMapping("/purchases/complex_query")
    public List<Object> getComplexQuery() {
        return purchaseService.complexPurchaseQuery();
    }

    @GetMapping("/purchases/same_address_after_winter")
    public List<Object> getSameAddressQuery() {
        return purchaseService.sameAddressQuery();
    }

    @GetMapping("/purchases/not_avtozavodsk")
    public List<Object> getNotAvtozavodskQuery() {
        return purchaseService.notAvtozavodskQuery();
    }

    @GetMapping("/purchases/bought_where_stored")
    public List<Object> getBoughtWhereStoredQuery() {
        return purchaseService.boughtWhereStoredQuery();
    }

    @GetMapping("/purchases/id_name_sum")
    public List<Object> getIdCustomerNameAndSumQuery() {
        return purchaseService.idCustomerNameAndSumQuery();
    }

    @GetMapping("/purchases/{id}")
    public ResponseEntity<Purchase> getPurchaseById(@PathVariable(value = "id")Integer id) throws ResourceNotFoundException {
        Purchase purchase = purchaseService.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Purchase not found for id: " + id)
        );
        return ResponseEntity.ok(purchase);
    }


    @DeleteMapping("/purchases/{id}")
    public Map<String, Boolean> deletePurchase(@PathVariable (value = "id")Integer id) throws ResourceNotFoundException {
        Purchase purchase = purchaseService.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Purchase not found for id: " + id)
        );
        purchaseService.delete(purchase);

        Map<String, Boolean> response = new HashMap<>();
        response.put("deleted", true);
        return response;
    }

    @PatchMapping(path = "/purchases/{id}", consumes = "application/json-patch+json")
    public ResponseEntity<Purchase> updatePurchase(@PathVariable Integer id, @RequestBody JsonPatch patch)
            throws ResourceNotFoundException{
        try {
            Purchase purchase = purchaseService.findById(id).orElseThrow(
                    () -> new ResourceNotFoundException("Purchase not found for id: " + id));
            Purchase purchasePatched = applyPatchToPurchase(patch, purchase);
            purchaseService.addPurchase(purchasePatched);
            return ResponseEntity.ok(purchasePatched);
        } catch (JsonPatchException | JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private Purchase applyPatchToPurchase(
            JsonPatch patch, Purchase targetPurchase) throws JsonPatchException, JsonProcessingException {
        JsonNode patched = patch.apply(objectMapper.convertValue(targetPurchase, JsonNode.class));
        return objectMapper.treeToValue(patched, Purchase.class);
    }

    @PostMapping("/purchases")
    public ResponseEntity<Purchase> postPurchase(@RequestBody Purchase purchase) {
        Purchase purchaseRes = this.purchaseService.addPurchase(purchase);

        return new ResponseEntity<>(purchaseRes, HttpStatus.OK);
    }

    @PutMapping("/purchases/{id}")
    Purchase replacePurchase(@RequestBody Purchase purchase, @PathVariable Integer id) {
        return purchaseService.findById(id)
                .map(purchaseRes -> {
                    purchaseRes.setDate(purchase.getDate());
                    purchaseRes.setNum(purchase.getNum());
                    purchaseRes.setSum(purchase.getSum());
                    return purchaseService.addPurchase(purchaseRes);
                })
                .orElseGet(() -> {
                    return purchaseService.addPurchase(purchase);
                });
    }

}
