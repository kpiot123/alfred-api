package eu.xenit.apix.webscripts.arguments;


import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

public class WebScriptResponseArgumentResolver extends AbstractTypeBasedArgumentResolver<WebScriptResponse> {

    @Override
    protected Class<?> getExpectedArgumentType() {
        return WebScriptResponse.class;
    }

    @Override
    public WebScriptResponse resolveArgument(final WebScriptRequest request, final WebScriptResponse response) {
        return response;
    }
}
