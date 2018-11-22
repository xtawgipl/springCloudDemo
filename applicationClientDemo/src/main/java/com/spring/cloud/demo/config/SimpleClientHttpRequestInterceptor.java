package com.spring.cloud.demo.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.AbstractClientHttpResponse;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * restTemplate 拦截器
 * @author zhangjj
 * @create 2018-11-08 18:20
 **/
@Component
public class SimpleClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {

    private Logger logger = LoggerFactory.getLogger(SimpleClientHttpRequestInterceptor.class);

    @Autowired
    private DiscoveryClient discoveryClient;

    private ConcurrentHashMap<String, List<ServiceInstance>> servicesMap = new ConcurrentHashMap<>();

    @Scheduled(fixedRate = 2 * 1000)
    public void loadConfig() {
        logger.info("开始更新服务信息。。。");
        List<String> services = discoveryClient.getServices();
        for(String service : services){
            List<ServiceInstance> instances = discoveryClient.getInstances(service);
            servicesMap.put(service, instances);
        }
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        System.out.println("SimpleClientHttpRequestInterceptor.......");
        List<ServiceInstance> instances = new ArrayList<>(servicesMap.get("service-discovery-demo"));//写时拷贝
        Random random = new Random();
        int index = random.nextInt(instances.size());
        String address = instances.get(index).getUri().toURL().toString() + request.getURI().toString();
        logger.debug("调用 address = " + address);
        URL url = new URL(address);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        SimpleClientHttpResponse clientHttpResponse = new SimpleClientHttpResponse(urlConnection);
        return clientHttpResponse;
    }

    class SimpleClientHttpResponse extends AbstractClientHttpResponse {

        private final HttpURLConnection connection;

        @Nullable
        private HttpHeaders headers;

        @Nullable
        private InputStream responseStream;


        SimpleClientHttpResponse(HttpURLConnection connection) {
            this.connection = connection;
        }


        @Override
        public int getRawStatusCode() throws IOException {
            return this.connection.getResponseCode();
        }

        @Override
        public String getStatusText() throws IOException {
            return this.connection.getResponseMessage();
        }

        @Override
        public HttpHeaders getHeaders() {
            if (this.headers == null) {
                this.headers = new HttpHeaders();
                // Header field 0 is the status line for most HttpURLConnections, but not on GAE
                String name = this.connection.getHeaderFieldKey(0);
                if (StringUtils.hasLength(name)) {
                    this.headers.add(name, this.connection.getHeaderField(0));
                }
                int i = 1;
                while (true) {
                    name = this.connection.getHeaderFieldKey(i);
                    if (!StringUtils.hasLength(name)) {
                        break;
                    }
                    this.headers.add(name, this.connection.getHeaderField(i));
                    i++;
                }
            }
            return this.headers;
        }

        @Override
        public InputStream getBody() throws IOException {
            InputStream errorStream = this.connection.getErrorStream();
            this.responseStream = (errorStream != null ? errorStream : this.connection.getInputStream());
            return this.responseStream;
        }

        @Override
        public void close() {
            if (this.responseStream != null) {
                try {
                    StreamUtils.drain(this.responseStream);
                    this.responseStream.close();
                }
                catch (Exception ex) {
                    // ignore
                }
            }
        }

    }
}
