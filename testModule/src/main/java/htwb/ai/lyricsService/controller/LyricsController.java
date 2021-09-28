package htwb.ai.testModule.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/thirdService")
public class FirstController {
	
	@GetMapping("/")
	public String test() {
		return "Hello JavaInUse Called in Third Service";
	}

}
