package eskimo.backend.judge;

import java.net.URI;

/**
 * Created by Stepan Klevleev on 16-Aug-16.
 */
class Invoker {

    private URI uri;

    public URI getUri() {
        return uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }

    String getCompileUrl() {
        return uri.toString() + "/invoke/compile";
    }

    String getTestUrl() {
        return uri.toString() + "/invoke/run-test";
    }

}
