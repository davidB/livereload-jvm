package net_alchim31_livereload;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import org.json.simple.JSONValue;

/**
 * @see http://feedback.livereload.com/knowledgebase/articles/86174-livereload-protocol
 * @author dwayne
 *
 */
public class LRProtocol {

  public String hello() {
    LinkedList<String> protocols = new LinkedList<String>();
    protocols.add("http://livereload.com/protocols/official-7");

    LinkedHashMap<String, Object> obj = new LinkedHashMap<String, Object>();
    obj.put("command","hello");
    obj.put("protocols", protocols);
    obj.put("serverName", "livereload-jvm");
    return JSONValue.toJSONString(obj);
  }
  
  public String alert(String msg) throws Exception {
    LinkedHashMap<String, Object> obj = new LinkedHashMap<String, Object>();
    obj.put("command","alert");
    obj.put("message", msg);
    return JSONValue.toJSONString(obj);
  }
  
  public String reload(String path) throws Exception {
    LinkedHashMap<String, Object> obj = new LinkedHashMap<String, Object>();
    obj.put("command","reload");
    obj.put("path", path);
    obj.put("liveCSS", true);
    return JSONValue.toJSONString(obj);
  }
  
  @SuppressWarnings("unchecked")
  public boolean isHello(String data) throws Exception {
    Object obj= JSONValue.parse(data);
    boolean back = obj instanceof Map;
    back = back && "hello".equals(((Map<Object,Object>)obj).get("command"));
    return back;
  } 

}
