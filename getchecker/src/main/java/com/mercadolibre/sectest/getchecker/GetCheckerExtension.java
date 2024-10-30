/**
 * The GetCheckerExtension class implements the BurpExtension and HttpHandler interfaces.
 * This extension logs HTTP requests and responses, and checks if a POST request can be
 * converted to a GET request with the same parameters.
 * 
 * <p>It initializes the extension, registers HTTP handlers, and sets up a user interface
 * tab for logging issues detected during the HTTP request/response cycle.</p>
 * 
 * <p>Methods:</p>
 * <ul>
 *   <li>{@link #initialize(MontoyaApi)}: Initializes the extension, sets up logging, and registers HTTP handlers.</li>
 *   <li>{@link #handleHttpRequestToBeSent(HttpRequestToBeSent)}: Handles HTTP requests to be sent, allowing them to continue.</li>
 *   <li>{@link #handleHttpResponseReceived(HttpResponseReceived)}: Handles HTTP responses received, checks for POST requests, and attempts to convert them to GET requests.</li>
 *   <li>{@link #constructLoggerTab(IssueTableModel)}: Constructs the user interface tab for logging issues.</li>
 * </ul>
 * 
 * <p>Fields:</p>
 * <ul>
 *   <li>{@code logging}: Used for logging messages and events.</li>
 *   <li>{@code api}: The Montoya API instance for interacting with Burp Suite.</li>
 *   <li>{@code tableModel}: The model for the table displaying logged issues.</li>
 * </ul>
 * 
 * <p>Inner Classes:</p>
 * <ul>
 *   <li>{@code Issue}: Represents an issue detected during the HTTP request/response cycle.</li>
 * </ul>
 * 
 * <p>Usage:</p>
 * <pre>
 * {@code
 * GetCheckerExtension extension = new GetCheckerExtension();
 * extension.initialize(api);
 * }
 * </pre>
 * 
 * <p>Note: This extension is designed to work with Burp Suite and the Montoya API.</p>
 */
package com.mercadolibre.sectest.getchecker;

import java.util.Date;
import java.util.concurrent.CompletableFuture;

import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;

import com.mercadolibre.sectest.suitetab.Issue;
import com.mercadolibre.sectest.suitetab.IssueTableModel;

import java.awt.Component;

import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;
import burp.api.montoya.http.handler.HttpHandler;
import burp.api.montoya.http.handler.HttpRequestToBeSent;
import burp.api.montoya.http.handler.HttpResponseReceived;
import burp.api.montoya.http.handler.RequestToBeSentAction;
import burp.api.montoya.http.handler.ResponseReceivedAction;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.logging.Logging;
import burp.api.montoya.ui.UserInterface;
import burp.api.montoya.ui.editor.HttpRequestEditor;
import burp.api.montoya.ui.editor.HttpResponseEditor;
import static burp.api.montoya.ui.editor.EditorOptions.READ_ONLY;

public class GetCheckerExtension implements BurpExtension, HttpHandler {
    
    private Logging logging;
    private MontoyaApi api;
    private IssueTableModel tableModel;

    @Override
    public void initialize(MontoyaApi api) {
        this.api = api;
        api.extension().setName("Get Checker Extension");
        logging = api.logging();
        api.http().registerHttpHandler(this);

        tableModel = new IssueTableModel();
        api.userInterface().registerSuiteTab("Issue Logger", constructLoggerTab(tableModel));

        logging.logToOutput("Get Checker Extension V0 Loaded");
    }

    @Override
    public RequestToBeSentAction handleHttpRequestToBeSent(HttpRequestToBeSent requestToBeSent) {
        return RequestToBeSentAction.continueWith(requestToBeSent);
    }

    @Override
    public ResponseReceivedAction handleHttpResponseReceived(HttpResponseReceived responseReceived) {

        //Get the original request
        HttpRequest request = responseReceived.initiatingRequest();

        //Check if the request is a POST request with Content-Type application/x-www-form-urlencoded
        if (request.method().equals("POST")
                && request.headers().stream().anyMatch(header -> header.name().equals("Content-Type")
                        && header.value().equals("application/x-www-form-urlencoded"))) {
            
            logging.raiseInfoEvent("Candidate POST Request found: " + request.url());
            //Get the body of the POST request
            String body = request.bodyToString();
            //Get the URL of the POST request
            String url = request.url();
            //Create a GET request with the same URL and body
            String getUrl = url + "?" + body;
            //Create a GET request
            HttpRequest getRequest = HttpRequest.httpRequestFromUrl(getUrl);

            //Send the GET request asynchronously
            CompletableFuture.supplyAsync(() -> api.http().sendRequest(getRequest))
                    .thenAccept(getResponse -> {
                        //Check if the POST request returned a 2xx status code and the GET request returned a 2xx status code
                        if (getResponse.response().statusCode() >= 200 && getResponse.response().statusCode() < 300) {
                            //If the POST request and GET request returned a 2xx status code, log the issue
                            String message = "POST request to " + url + " returned HTTP " + responseReceived.statusCode() + ", GET request to " + getUrl + " returned " + getResponse.response().statusCode();
                            logging.raiseInfoEvent("ISSUE DETECTED: " + message);
                            tableModel.add(
                                new Issue(new Date(), "GET Checker", url, body, message, request, responseReceived)
                                );
                        }
                    });
        }
        return ResponseReceivedAction.continueWith(responseReceived);
    }


    private Component constructLoggerTab(IssueTableModel tableModel){
        // Split pane with log entries on the left and request/response viewers on the right
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

        // Tabs for request and response viewers
        JTabbedPane tabs = new JTabbedPane();

        // Get the user interface
        UserInterface userInterface = api.userInterface();

        // Request and response viewers
        HttpRequestEditor requestViewer = userInterface.createHttpRequestEditor(READ_ONLY);
        HttpResponseEditor responseViewer = userInterface.createHttpResponseEditor(READ_ONLY);

        // Add the request and response viewers to the tabs
        tabs.addTab("Request", requestViewer.uiComponent());
        tabs.addTab("Response", responseViewer.uiComponent());
        splitPane.setRightComponent(tabs);

        // Table for log entries
        JTable table = new JTable(tableModel){
            @Override
            public void changeSelection(int rowIndex, int columnIndex, boolean toggle, boolean extend){
                // Show the log entry for the selected row
                Issue issue = tableModel.get(rowIndex);
                requestViewer.setRequest(issue.getRequest());
                responseViewer.setResponse(issue.getResponse());
                super.changeSelection(rowIndex, columnIndex, toggle, extend);
            }
        };

        // Add the table to a scroll pane
        JScrollPane scrollPane = new JScrollPane(table);

        // Add the scroll pane to the split pane
        splitPane.setLeftComponent(scrollPane);

        return splitPane;
    }
}