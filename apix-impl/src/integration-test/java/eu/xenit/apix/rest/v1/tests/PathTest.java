package eu.xenit.apix.rest.v1.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import eu.xenit.apix.data.NodeRef;
import java.io.IOException;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.namespace.NamespaceService;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.apache.http.util.EntityUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Created by kenneth on 16.03.16.
 */
public class PathTest extends BaseTest {

    private final static Logger logger = LoggerFactory.getLogger(PathTest.class);

    @Autowired
    @Qualifier("NodeService")
    NodeService nodeService;

    @Autowired
    @Qualifier("PermissionService")
    PermissionService permissionService;

    @Autowired
    @Qualifier("NamespaceService")
    NamespaceService namespaceService;

    @Before
    public void setup() {
        AuthenticationUtil.setAdminUserAsFullyAuthenticatedUser();
    }

    @Test
    public void testDisplayPathGet() throws IOException {
        NodeRef[] nodeRef = init();
        String url = makeNodesUrl(nodeRef[0], "/path", "admin", "admin");

        HttpResponse httpResponse = Request.Get(url).execute().returnResponse();
        String result = EntityUtils.toString(httpResponse.getEntity());
        logger.debug(" DisplayPath :" + result);

        assertEquals(200, httpResponse.getStatusLine().getStatusCode());

        String expectedResult = String.format("\"displayPath\":\"%s\"",
                this.nodeService.getPath(new org.alfresco.service.cmr.repository.NodeRef(nodeRef[0].getValue()))
                        .toDisplayPath(this.nodeService, this.permissionService));
        assertTrue(result.contains(expectedResult));
    }

    @Test
    public void testQNamePathGet() throws IOException {
        NodeRef[] nodeRef = init();
        String url = makeNodesUrl(nodeRef[0], "/path", "admin", "admin");

        HttpResponse httpResponse = Request.Get(url).execute().returnResponse();
        String result = EntityUtils.toString(httpResponse.getEntity());
        logger.debug(" QNamePath :" + result);

        assertEquals(200, httpResponse.getStatusLine().getStatusCode());
        String expectedResult = String.format("\"qnamePath\":\"%s\"",
                this.nodeService.getPath(new org.alfresco.service.cmr.repository.NodeRef(nodeRef[0].getValue()))
                        .toPrefixString(namespaceService));
        assertTrue(result.contains(expectedResult));
    }

    @After
    public void cleanUp() {
        this.removeMainTestFolder();
    }
}
