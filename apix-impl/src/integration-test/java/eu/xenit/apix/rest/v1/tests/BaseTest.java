package eu.xenit.apix.rest.v1.tests;

import static org.junit.Assert.assertEquals;

import eu.xenit.apix.integrationtesting.runner.ApixIntegration;
import eu.xenit.apix.tests.ApixImplBundleFilter;
import eu.xenit.testing.integrationtesting.runner.UseSpringContextOfBundle;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.admin.SysAdminParams;
import org.alfresco.repo.model.Repository;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.coci.CheckOutCheckInService;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.extensions.surf.util.URLEncoder;

/**
 * Created by kenneth on 14.03.16.
 */
@RunWith(ApixIntegration.class)
@UseSpringContextOfBundle(filter = ApixImplBundleFilter.class)
public abstract class BaseTest {

    private final static Logger logger = LoggerFactory.getLogger(BaseTest.class);
    private final static String VERSION = "v1";

    @Autowired
    protected ServiceRegistry serviceRegistry;

    @Autowired
    protected Repository repository;

    @Autowired
    @Qualifier("AuthenticationService")
    protected AuthenticationService authenticationService;

    @Autowired
    SysAdminParams sysAdminParams;

    // This is a method so it can be overrided in v2
    // It's not static like the string because you can't override static methods :(
    protected String getVersion() {
        return VERSION;
    }

    protected String createApixUrl(String subUrl, Object... args) {
        for (int i = 0; i < args.length; i++) {
            args[i] = URLEncoder.encodeUriComponent(args[i].toString());
        }
        return makeAlfrescoBaseurlAdmin() + "/apix/" + getVersion() + String.format(subUrl, args);
    }

    protected String makeAlfrescoBaseurlAdmin() {
        return makeAlfrescoBaseurl("admin", "admin");
    }

    protected String makeAlfrescoBaseurl(String userName, String passWord) {

        String protocol = "http";  // sysAdminParams.getAlfrescoProtocol();
        String host = "localhost"; // sysAdminParams.getAlfrescoHost();
        String port = "8080"; //  Integer.toString(sysAdminParams.getAlfrescoPort());

        //String protocol = sysAdminParams.getAlfrescoProtocol();
        //String host = sysAdminParams.getAlfrescoHost();
        //String port = Integer.toString(sysAdminParams.getAlfrescoPort());

        String base = protocol + "://" + userName + ":" + passWord + "@" + host + ":" + port + "/alfresco/s";
        return base;
    }

    protected String makeAlfrescoBaseurl() {
        String base = sysAdminParams.getAlfrescoProtocol() + "://"
                + sysAdminParams.getAlfrescoHost()
                + ":" + sysAdminParams.getAlfrescoPort()
                + "/alfresco/s";

        return base;
    }

    protected String makeNodesUrl(String space, String store, String guid, String action, String userName,
            String passWord) {
        return String.format(makeAlfrescoBaseurl(userName, passWord) + "/apix/%s/nodes/%s/%s/%s%s", getVersion(), space,
                store, guid, action);
    }

    protected String makeFormsUrl(String space, String store, String guid, String action, String userName,
            String passWord) {
        return String
                .format(makeAlfrescoBaseurl(userName, passWord) + "/apix/%s/temp/forms/%s/%s/%s%s", getVersion(), space,
                        store, guid, action);
    }

    protected String makeFormsUrl(eu.xenit.apix.data.NodeRef nodeRef, String action, String userName, String passWord) {
        String space = nodeRef.getStoreRefProtocol();
        String store = nodeRef.getStoreRefId();
        String guid = nodeRef.getGuid();

        return this.makeFormsUrl(space, store, guid, action, userName, passWord);
    }

    protected String makeFormsUrl(eu.xenit.apix.data.NodeRef nodeRef, String userName, String passWord) {
        return this.makeFormsUrl(nodeRef, "", userName, passWord);
    }

    protected String makeFormsUrl(eu.xenit.apix.data.NodeRef nodeRef) {
        return this.makeFormsUrl(nodeRef, "admin", "admin");
    }

    protected String makeWorkingCopiesUrl(String space, String store, String guid, String action, String userName,
            String passWord) {
        return String
                .format(makeAlfrescoBaseurl(userName, passWord) + "/apix/%s/workingcopies/%s/%s/%s%s", getVersion(),
                        space, store, guid, action);
    }

    protected String makeWorkingCopiesUrl(eu.xenit.apix.data.NodeRef nodeRef, String action, String userName,
            String passWord) {
        String space = nodeRef.getStoreRefProtocol();
        String store = nodeRef.getStoreRefId();
        String guid = nodeRef.getGuid();

        return this.makeWorkingCopiesUrl(space, store, guid, action, userName, passWord);
    }

    protected String makeWorkingCopiesUrl(String space, String store, String guid, String userName, String passWord) {
        return this.makeWorkingCopiesUrl(space, store, guid, "", userName, passWord);
    }

    protected String makeNodesUrlWithTicket(String space, String store, String guid, String action) {
        String ticket = authenticationService.getCurrentTicket();
        return String
                .format(makeAlfrescoBaseurl() + "/apix/%s/nodes/%s/%s/%s%s?alf_ticket=%s", getVersion(), space, store,
                        guid, action, ticket);
    }

    protected String makeBulkUrl() {
        return String.format(makeAlfrescoBaseurl("admin", "admin") + "/apix/" + getVersion() + "/bulk");
    }

    protected String makeBulkUrlWithTicket() {
        String ticket = authenticationService.getCurrentTicket();
        return String.format(makeAlfrescoBaseurl() + "/apix/" + getVersion() + "/bulk?alf_ticket=" + ticket);
    }

    protected eu.xenit.apix.data.NodeRef[] init() {
        final eu.xenit.apix.data.NodeRef[] ref = {null, null, null};
        TransactionService transactionService = serviceRegistry.getTransactionService();

        this.removeMainTestFolder();

        RetryingTransactionHelper.RetryingTransactionCallback<Object> txnWork = new RetryingTransactionHelper.RetryingTransactionCallback<Object>() {
            public Object execute() throws Exception {
                NodeRef companyHomeRef = repository.getCompanyHome();
                FileInfo mainTestFolder = createTestNode(companyHomeRef, "mainTestFolder", ContentModel.TYPE_FOLDER);
                FileInfo testFolder = createTestNode(mainTestFolder.getNodeRef(), "testFolder",
                        ContentModel.TYPE_FOLDER);
                FileInfo testNode = createTestNode(testFolder.getNodeRef(), "testFile", ContentModel.TYPE_CONTENT);
                NodeRef testNodeRef = testNode.getNodeRef();
                eu.xenit.apix.data.NodeRef apixTestNodeRef = new eu.xenit.apix.data.NodeRef(testNodeRef.toString());
                ref[0] = apixTestNodeRef;

                FileInfo testFolder2 = createTestNode(mainTestFolder.getNodeRef(), "testFolder2",
                        ContentModel.TYPE_FOLDER);
                FileInfo testNode2 = createTestNode(testFolder2.getNodeRef(), "testFile2", ContentModel.TYPE_CONTENT);
                NodeRef testNodeRef2 = testNode2.getNodeRef();
                eu.xenit.apix.data.NodeRef apixTestNodeRef2 = new eu.xenit.apix.data.NodeRef(testNodeRef2.toString());
                ref[1] = apixTestNodeRef2;

                FileInfo testNode3 = createTestNode(testFolder2.getNodeRef(), "testFile3", ContentModel.TYPE_CONTENT);
                NodeRef testNodeRef3 = testNode3.getNodeRef();
                eu.xenit.apix.data.NodeRef apixTestNodeRef3 = new eu.xenit.apix.data.NodeRef(testNodeRef3.toString());
                ref[2] = apixTestNodeRef3;
                return null;
            }
        };

        transactionService.getRetryingTransactionHelper().doInTransaction(txnWork, false, true);
        return ref;
    }

    protected String makeNodesUrl(eu.xenit.apix.data.NodeRef nodeRef, String userName, String passWord) {
        return this.makeNodesUrl(nodeRef, "", userName, passWord);
    }

    protected String makeNodesUrlWithTicket(eu.xenit.apix.data.NodeRef nodeRef) {
        return this.makeNodesUrlWithTicket(nodeRef, "");
    }

    protected String makeNodesUrl(eu.xenit.apix.data.NodeRef nodeRef, String action, String userName, String passWord) {
        String space = nodeRef.getStoreRefProtocol();
        String store = nodeRef.getStoreRefId();
        String guid = nodeRef.getGuid();
        return this.makeNodesUrl(space, store, guid, action, userName, passWord);
    }

    protected String makeNodesUrlWithTicket(eu.xenit.apix.data.NodeRef nodeRef, String action) {
        String space = nodeRef.getStoreRefProtocol();
        String store = nodeRef.getStoreRefId();
        String guid = nodeRef.getGuid();
        return this.makeNodesUrlWithTicket(space, store, guid, action);
    }

    protected String makeNodesUrl(String guid, String action, String userName, String passWord) {
        String space = "workspace";
        String store = "SpacesStore";

        return this.makeNodesUrl(space, store, guid, action, userName, passWord);
    }

    protected String makeNodesUrlWithTicket(String guid, String action) {
        String space = "workspace";
        String store = "SpacesStore";

        return this.makeNodesUrlWithTicket(space, store, guid, action);
    }

    protected NodeRef getMainTestFolder() {
        FileFolderService fileFolderService = this.serviceRegistry.getFileFolderService();
        NodeRef nodeRef = fileFolderService.searchSimple(repository.getCompanyHome(), "mainTestFolder");
        return nodeRef;
    }

    protected FileInfo createTestNode(NodeRef parentRef, String name, QName type) {
        FileFolderService fileFolderService = this.serviceRegistry.getFileFolderService();

        FileInfo testNode = fileFolderService.create(parentRef, name, type);
        return testNode;
    }

    protected boolean removeTestNode(NodeRef nodeRef) {
        NodeService alfrescoNodeService = this.serviceRegistry.getNodeService();
        CheckOutCheckInService checkOutCheckInService = this.serviceRegistry.getCheckOutCheckInService();
        boolean success = false;

        if (alfrescoNodeService.exists(nodeRef)) {
            List<NodeRef> childRefs = new ArrayList<>();
            this.getChildrenRecursive(nodeRef, childRefs);
            for (NodeRef childRef : childRefs) {
                if (checkOutCheckInService.isWorkingCopy(childRef)) {
                    checkOutCheckInService.cancelCheckout(childRef);
                }
            }

            alfrescoNodeService.deleteNode(nodeRef);
            success = true;
        }

        return success;
    }

    private void getChildrenRecursive(NodeRef nodeRef, List<NodeRef> childRefs) {
        NodeService alfrescoNodeService = this.serviceRegistry.getNodeService();

        List<ChildAssociationRef> childAssocs = alfrescoNodeService.getChildAssocs(nodeRef);
        if (childAssocs.size() > 0) {
            for (ChildAssociationRef childAssoc : childAssocs) {
                NodeRef childRef = childAssoc.getChildRef();
                this.getChildrenRecursive(childRef, childRefs);
            }
        } else {
            childRefs.add(nodeRef);
        }
    }

    protected void removeMainTestFolder() {
        RetryingTransactionHelper.RetryingTransactionCallback<Object> txnWork = new RetryingTransactionHelper.RetryingTransactionCallback<Object>() {
            public Object execute() throws Exception {
                try {
                    NodeRef nodeRef = getMainTestFolder();
                    removeTestNode(nodeRef);
                } catch (RuntimeException ex) {
                    logger.debug("Did not need to remove mainTestFolder because it did not exist");
                    //ex.printStackTrace();
                }
                return null;
            }
        };
        TransactionService transactionService = serviceRegistry.getTransactionService();
        transactionService.getRetryingTransactionHelper().doInTransaction(txnWork, false, true);
    }

    public String json(String str) {
        return str.replaceAll("'", "\"");
    }

    public <T> T doPost(String checkoutUrl, Class<T> returnType, String jsonBody, Object... args) throws IOException {
        return doWithBody(new HttpPost(checkoutUrl), returnType, jsonBody, args);
    }

    public <T> T doPut(String checkoutUrl, Class<T> returnType, String jsonBody, Object... args) throws IOException {
        return doWithBody(new HttpPut(checkoutUrl), returnType, jsonBody, args);
    }

    private <T> T doWithBody(HttpEntityEnclosingRequestBase req, Class<T> returnType, String jsonBody, Object... args)
            throws IOException {
        final CloseableHttpClient checkoutHttpclient = HttpClients.createDefault();
        if (jsonBody != null) {
            String checkoutJsonString = json(String.format(jsonBody, args));
            req.setEntity(new StringEntity(checkoutJsonString));
        }

        try (CloseableHttpResponse response = checkoutHttpclient.execute(req)) {
            String result = EntityUtils.toString(response.getEntity());
            assertEquals(200, response.getStatusLine().getStatusCode());
            if (returnType == null) {
                return null;
            }
            T ret = new ObjectMapper().readValue(result, returnType);
            return ret;

        }
    }

    public <T> T doGet(String checkoutUrl, Class<T> returnType) throws IOException {
        final HttpResponse response = Request.Get(checkoutUrl).execute().returnResponse();
        String result = EntityUtils.toString(response.getEntity());

        assertEquals(200, response.getStatusLine().getStatusCode());
        if (returnType == null) {
            return null;
        }
        T ret = new ObjectMapper().readValue(result, returnType);
        return ret;
    }

    /*public <T> T doDelete(String checkoutUrl, Class<T> returnType) throws IOException {
        return doDelete(checkoutUrl, returnType, null);
    }*/

    public <T> T doDelete(String url, Class<T> returnType) throws IOException {
        //String jsonString = json(String.format(jsonBody, args));
        Request delete = Request.Delete(url);
        /*if (jsonBody != null)
            delete = delete.bodyString(jsonString, ContentType.APPLICATION_JSON);*/
        final HttpResponse response = delete.execute().returnResponse();

        String result = EntityUtils.toString(response.getEntity());

        assertEquals(200, response.getStatusLine().getStatusCode());

        if (returnType == null) {
            return null;
        }
        T ret = new ObjectMapper().readValue(result, returnType);
        return ret;
    }


}
