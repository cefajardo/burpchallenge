/**
 * The PIIScanner class implements the BurpExtension and HttpHandler interfaces to create a Burp Suite extension
 * that scans HTTP responses for Personally Identifiable Information (PII) patterns, specifically CPF numbers.
 * 
 * <p>This extension logs any detected PII and displays the information in a custom tab within the Burp Suite UI.
 * 
 * <p>Features:
 * <ul>
 *   <li>Initializes the extension and registers HTTP handlers.</li>
 *   <li>Scans HTTP responses for CPF patterns and logs detected issues.</li>
 *   <li>Displays detected issues in a custom tab with request and response viewers.</li>
 * </ul>
 * 
 * <p>Dependencies:
 * <ul>
 *   <li>Burp Suite Montoya API</li>
 *   <li>Java Swing components for UI</li>
 * </ul>
 * 
 * <p>Usage:
 * <pre>
 * {@code
 * PIIScanner scanner = new PIIScanner();
 * scanner.initialize(api);
 * }
 * </pre>
 * 
 * <p>Example:
 * <pre>
 * {@code
 * // Example of CPF pattern: 123.456.789-00
 * }
 * </pre>
 * 
 * @see burp.api.montoya.BurpExtension
 * @see burp.api.montoya.http.handler.HttpHandler
 * @see burp.api.montoya.MontoyaApi
 * @see burp.api.montoya.logging.Logging
 * @see burp.api.montoya.ui.UserInterface
 * @see burp.api.montoya.ui.editor.HttpRequestEditor
 * @see burp.api.montoya.ui.editor.HttpResponseEditor
 */
package com.mercadolibre.sectest.piiscanner;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import burp.api.montoya.logging.Logging;
import burp.api.montoya.ui.UserInterface;
import burp.api.montoya.ui.editor.HttpRequestEditor;
import burp.api.montoya.ui.editor.HttpResponseEditor;
import static burp.api.montoya.ui.editor.EditorOptions.READ_ONLY;


public class PIIScanner implements BurpExtension, HttpHandler {

    // CPF pattern to be used in the PII Scanner Extension in the format 'XXX.XXX.XXX-XX'
    private static final Pattern CPF_PATTERN = Pattern.compile("\\b\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}\\b");
    
    private Logging logging;
    private MontoyaApi api;

    private IssueTableModel tableModel;

    @Override
    public void initialize(MontoyaApi api) {
        this.api = api;
        api.extension().setName("PII Scanner Extension");
        logging = api.logging();
        api.http().registerHttpHandler(this);

        tableModel = new IssueTableModel();
        api.userInterface().registerSuiteTab("Issue Logger", constructLoggerTab(tableModel));

        logging.logToOutput("PII Scanner Extension V0 Loaded");
    }

    @Override
    public RequestToBeSentAction handleHttpRequestToBeSent(HttpRequestToBeSent requestToBeSent) {
        return RequestToBeSentAction.continueWith(requestToBeSent);
    }

    @Override
    public ResponseReceivedAction handleHttpResponseReceived(HttpResponseReceived responseReceived) {
        // Evaluate the response body for CPF patterns
        Matcher matcher = CPF_PATTERN.matcher(responseReceived.bodyToString() );
        // Log any detected CPF patterns
        while (matcher.find()) {
            // Log the detected issue
            String message = "CPF found in response body " + matcher.group();
            logging.raiseInfoEvent("ISSUE DETECTED: " + message);
            tableModel.add(
                new Issue( new Date(), "PII Scanner", responseReceived.initiatingRequest().url(), 
                    responseReceived.initiatingRequest().bodyToString(), message, 
                    responseReceived.initiatingRequest(), responseReceived) );
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