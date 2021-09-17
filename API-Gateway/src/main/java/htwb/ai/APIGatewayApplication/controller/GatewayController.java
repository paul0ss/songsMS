package htwb.ai.APIGatewayApplication.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class GatewayController {
	
	@GetMapping("/")
	public String test() {
		return "Hello JavaInUse Called in Controller Service";
	}
}
