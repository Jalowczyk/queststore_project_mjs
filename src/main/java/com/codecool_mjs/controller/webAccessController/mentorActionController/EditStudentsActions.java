package com.codecool_mjs.controller.webAccessController.mentorActionController;

import com.codecool_mjs.controller.applicationActionsController.CodecoolerController;
import com.codecool_mjs.controller.webAccessController.Sessionable;
import com.codecool_mjs.controller.webAccessController.WebActionController;
import com.codecool_mjs.dataaccess.dao.DaoException;
import com.codecool_mjs.model.Codecooler;
import com.codecool_mjs.utilities.FormResolver;
import com.codecool_mjs.utilities.UriResolver;
import com.sun.net.httpserver.HttpExchange;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Map;

public class EditStudentsActions extends WebActionController implements Sessionable {

    private static String CONFIRMATION_TEMPLATE_URL = "mentor/edit-confirmation";
    private static String DATA_TEMPLATE_URL = "mentor/edit-student";
    private CodecoolerController codecoolerController;

    public EditStudentsActions(){
        super();
        codecoolerController = CodecoolerController.getInstance();

    }

    private String editStudentAction(Integer id) throws DaoException {

        Codecooler codecooler = codecoolerController.getCodecoolerById(id);
        setVariable("student", codecooler);

        return processTemplate(DATA_TEMPLATE_URL);
    }

    @Override
    public String getAccessType() {
        return "Mentor";
    }

    @Override
    public void sendPageForProperAccess(HttpExchange httpExchange) throws IOException, DaoException {
        String responseBody = "";
        int responseCode = 200;

        String method = httpExchange.getRequestMethod();
        String idStr = UriResolver.getUserIdFromURI(httpExchange);

        if(method.equals("GET")) {
            Integer id = Integer.parseInt(idStr);
            responseBody = editStudentAction(id);
        }

        if(method.equals("POST")) {

            Map<String, String> records;

            InputStreamReader isr = new InputStreamReader(httpExchange.getRequestBody(), "UTF-8");
            BufferedReader br = new BufferedReader(isr);
            String dataForm = br.readLine();

            records = FormResolver.parseDataForm(dataForm);
            records.put("id", idStr);

            codecoolerController.editCodecooler(records);

            processTemplate(CONFIRMATION_TEMPLATE_URL);
        }

        httpExchange.sendResponseHeaders(responseCode, responseBody.getBytes().length);
        OutputStream os = httpExchange.getResponseBody();
        os.write(responseBody.getBytes());
        os.close();
    }
}
