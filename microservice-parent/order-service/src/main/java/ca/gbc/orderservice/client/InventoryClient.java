package ca.gbc.orderservice.client;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


//@FeignClient(value = "inventory-service", url = "${inventory.service.url}")

@FeignClient(value = "inventory", url = "${inventory.service.url}")
//yo agregre el -service aqui, el profe solo puso inventory
public interface InventoryClient {


    @RequestMapping(method = RequestMethod.GET, value = "/api/inventory")
    boolean isInStock(@RequestParam String skuCode, @RequestParam Integer quantity);
}