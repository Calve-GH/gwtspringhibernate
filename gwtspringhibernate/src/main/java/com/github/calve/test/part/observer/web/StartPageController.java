package com.github.calve.test.part.observer.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
class StartPageController {
	@RequestMapping("/index")
	String index() {
		return "Application";
	}
}
