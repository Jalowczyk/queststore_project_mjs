package com.codecool_mjs.controller.webAccessController.adminActionsController;

import com.codecool_mjs.controller.applicationActionsController.GroupController;
import com.codecool_mjs.controller.applicationActionsController.MentorController;
import com.codecool_mjs.dataaccess.dao.DaoException;
import com.codecool_mjs.model.Admin;
import com.codecool_mjs.model.Group;
import com.codecool_mjs.model.Mentor;
import com.codecool_mjs.utilities.FormResolver;
import com.codecool_mjs.utilities.UriResolver;
import com.codecool_mjs.view.webView.TemplatesProcessor;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class EditClassActions implements HttpHandler{

    private TemplatesProcessor templateProcessor;
    private Admin loggedUser;
    private GroupController groupController = GroupController.getInstance();

    public EditClassActions(){
        this.templateProcessor = new TemplatesProcessor();
    }

    public void setLoggedUser(Admin loggedUser) {
        this.loggedUser = loggedUser;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {

        String responseBody = "";
        int responseCode = 200;

        String method = httpExchange.getRequestMethod();
        String idStr = UriResolver.getUserIdFromURI(httpExchange);

        if(method.equals("GET")) {

            Integer id = Integer.parseInt(idStr);

            responseBody = editClass(id);
        }

        if(method.equals("POST")) {

            Map<String, String> records;

            InputStreamReader isr = new InputStreamReader(httpExchange.getRequestBody(), "UTF-8");
            BufferedReader br = new BufferedReader(isr);
            String dataForm = br.readLine();

            records = FormResolver.parseDataForm(dataForm);
            records.put("id", idStr);

            try {
                groupController.editGroup(records);
            } catch (DaoException e) {
                e.printStackTrace();
            }
            responseBody = templateProcessor.ProcessTemplateToPage("admin/edit-confirmation");
        }

        httpExchange.sendResponseHeaders(responseCode, responseBody.getBytes().length);
        OutputStream os = httpExchange.getResponseBody();
        os.write(responseBody.getBytes());
        os.close();
    }

    private String editClass(Integer id){

        Group group = null;

        Admin admin = new Admin(15,"Janusz", "Kowal", "j.k@cc.pl", "typoweHasło");
        Map<String, Object> variables = new HashMap<>();

        try {
            group = groupController.getGroup(id);
        } catch (DaoException e) {
            e.printStackTrace();
        }

        variables.put("user", admin);
        variables.put("class", group);

        templateProcessor.setVariables(variables);

        String page = templateProcessor.ProcessTemplateToPage("admin/edit-class");

        return page;
    }
}
