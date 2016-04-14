package com.github.dreamhead.moco;

import org.apache.http.message.BasicNameValuePair;
import org.junit.Test;

import java.io.IOException;

import static com.github.dreamhead.moco.Moco.*;
import static com.github.dreamhead.moco.Runner.running;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.remoteUrl;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.root;
import static org.apache.http.client.fluent.Request.Post;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MocoWebTest extends AbstractMocoHttpTest {
    @Test
    public void should_match_form_value() throws Exception {
        server.post(eq(form("name"), "dreamhead")).response("foobar");

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                String content = Post(root()).bodyForm(new BasicNameValuePair("name", "dreamhead")).execute().returnContent().asString();
                assertThat(content, is("foobar"));
            }
        });
    }

    @Test
    public void should_no_exception_form_get_request() throws Exception {
        server.request(eq(form("password"), "hello")).response("foobar");
        server.response("foobar");

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(helper.get(root()), is("foobar"));
            }
        });
    }

    @Test
    public void should_set_and_recognize_cookie() throws Exception {
        server.request(eq(cookie("loggedIn"), "true")).response(status(200));
        server.response(cookie("loggedIn", "true"), status(302));

        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                assertThat(helper.getForStatus(root()), is(302));
                assertThat(helper.getForStatus(root()), is(200));
            }
        });
    }

    @Test
    public void should_redirect_to_expected_url() throws Exception {
        server.get(by(uri("/"))).response("foo");
        server.get(by(uri("/redirectTo"))).redirectTo(root());

        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                assertThat(helper.get(remoteUrl("/redirectTo")), is("foo"));
            }
        });
    }

    @Test
    public void should_redirect_for_any_response() throws Exception {
        server.get(by(uri("/"))).response("foo");
        server.redirectTo(root());

        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                assertThat(helper.get(remoteUrl("/redirectTo")), is("foo"));
            }
        });
    }

    @Test
    public void should_download_attachment() throws Exception {
        server.get(by(uri("/"))).response(attachment("foo.txt", file("src/test/resources/foo.response")));

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(helper.get(remoteUrl("/")), is("foo.response"));
            }
        });
    }
}
