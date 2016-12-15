package me.jiangcai.dating.web.controller.manage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scripting.ScriptSource;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.scripting.support.StandardScriptEvaluator;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author CJ
 */
@PreAuthorize("hasAnyRole('ROOT','SCRIPT')")
@Controller
public class ManageScriptController {

    @Autowired
    private ApplicationContext applicationContext;

    @RequestMapping(method = RequestMethod.GET, value = "/manage/script")
    public String index() {
        return "manage/script.html";
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/manage/execScript")
    @Transactional
    public ResponseEntity<String> exec(String script) throws UnsupportedEncodingException {
        String type = "js";// 类型默认是js
        StandardScriptEvaluator scriptEvaluator = new StandardScriptEvaluator(getClass().getClassLoader());

        Map<String, Object> globals = new HashMap<>();
        globals.put("applicationContext", applicationContext);
        scriptEvaluator.setGlobalBindings(globals);

        ScriptSource scriptSource = new ResourceScriptSource(new ByteArrayResource(script.getBytes("UTF-8")) {
            @Override
            public String getFilename() {
                return System.currentTimeMillis() + "." + type;
            }
        });

        Object result = scriptEvaluator.evaluate(scriptSource);
        if (result == null)
            return ResponseEntity.ok(null);
        return ResponseEntity.ok().contentType(MediaType.valueOf(MediaType.TEXT_PLAIN_VALUE + ";charset=UTF-8"))
                .body(result.toString());
    }

}
