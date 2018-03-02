package pl.kwi.springboot.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import pl.kwi.springboot.commands.DeleteCommand;
import pl.kwi.springboot.services.LdapService;

@Controller
@RequestMapping(value="/delete")
public class DeleteController {
	
	@Autowired
	private LdapService ldapService;
	
	@RequestMapping(value="/{cn}")
	public String displayPage(@PathVariable String cn, @ModelAttribute("command")DeleteCommand command) {
		command.setName(ldapService.readUser(cn).getName());
		return "delete";
	}
	
	@RequestMapping(value="/handle-button-delete", method=RequestMethod.POST)
	public String handleButtonEdit(@ModelAttribute("command")DeleteCommand command) {
		ldapService.deleteUser(command.getCn());
		return "redirect:/list";
	}

}