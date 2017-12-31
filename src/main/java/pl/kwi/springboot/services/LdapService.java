package pl.kwi.springboot.services;

import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pl.kwi.springboot.entities.UserEntity;

@Service
public class LdapService {
	
	
	private static final String LDAP_DN = "dc=maxcrc,dc=com";
	
	@Autowired
	private LdapContext ldapContext;
	
	public void createUser(UserEntity user) {
		
		String uid = String.valueOf(user.getId());
		
        // entry's DN 
		String entryDN = String.format("uid=%s,ou=People,dc=maxcrc,dc=com", uid);  
	
	    // entry's attributes  	
		Attribute cn = new BasicAttribute("cn", user.getName()); 
		Attribute sn = new BasicAttribute("sn", user.getName());  
	    Attribute oc = new BasicAttribute("objectClass");  
	    oc.add("top");  
	    oc.add("person");  
	    oc.add("organizationalPerson");  
	    oc.add("inetOrgPerson");  
	
	    try {  

	    	BasicAttributes entry = new BasicAttributes();  
	    	entry.put(cn);
	    	entry.put(sn);  
	        entry.put(oc);  
	        ldapContext.createSubcontext(entryDN, entry);  
	
	    } catch (NamingException e) {  
	        System.err.println("save: error adding entry." + e);  
	    }
	    
	}
	
	public UserEntity readUser(Long uid){
		
		UserEntity user = null;
		
		//filter
		String filter = String.format("(uid=%s)", String.valueOf(uid));
		
		// search controls
		SearchControls sc = new SearchControls();
	    String[] attributeFilter = { "cn" };
	    sc.setReturningAttributes(attributeFilter);
	    sc.setSearchScope(SearchControls.SUBTREE_SCOPE);
	    
	    try {
			NamingEnumeration<SearchResult> results = ldapContext.search(LDAP_DN, filter, sc);
			String name = null;
			while (results.hasMore()) {
			      SearchResult sr = results.next();
			      Attributes attrs = sr.getAttributes();
			      name = (String)attrs.get("cn").get();
			      user = new UserEntity(uid, name);
			    }
		} catch (NamingException e) {
			System.err.println("load: error reading entry." + e);
		}
	    
	    return user;
	
	}
	
	public void updateUser(UserEntity user) {}
	
	public UserEntity deleteUser(Long uid){
		return null;
	}
	
	public List<UserEntity> getUserList(){
		
		List<UserEntity>  result = new ArrayList<UserEntity>();
		String name;
		Long uid;
		
		//filter
		String filter = "(uid=*)";
		
		// search controls
		SearchControls sc = new SearchControls();
	    String[] attributeFilter = { "uid", "cn" };
	    sc.setReturningAttributes(attributeFilter);
	    sc.setSearchScope(SearchControls.SUBTREE_SCOPE);
	    
	    try {
			NamingEnumeration<SearchResult> results = ldapContext.search(LDAP_DN, filter, sc);
			while (results.hasMore()) {
			      SearchResult sr = results.next();
			      Attributes attrs = sr.getAttributes();
			      uid = Long.valueOf((String)attrs.get("uid").get());
			      name = (String)attrs.get("cn").get();			      
			      result.add(new UserEntity(uid, name));
			    }
		} catch (NamingException e) {
			System.err.println("load: error reading entry." + e);
		}
	    
	    return result;
	
	}
	
	public Long generateUid() {
		List<UserEntity> users = getUserList();
		if (users.isEmpty()) {
			return 1L;
		}
		
		UserEntity user = users.get(users.size() - 1);
		long currentId = user.getId();
		return ++currentId;
	}

}