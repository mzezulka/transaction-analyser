package io.narayana.txdemo.tracing;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import io.jaegertracing.Configuration;
import io.jaegertracing.Configuration.CodecConfiguration;
import io.jaegertracing.Configuration.ReporterConfiguration;
import io.jaegertracing.Configuration.SamplerConfiguration;
import io.jaegertracing.Configuration.SenderConfiguration;
import io.jaegertracing.internal.JaegerSpanContext;
import io.jaegertracing.internal.JaegerTracer;
import io.jaegertracing.internal.JaegerTracer.Builder;
import io.jaegertracing.internal.propagation.TextMapCodec;
import io.jaegertracing.spi.Extractor;
import io.jaegertracing.spi.Injector;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import io.opentracing.propagation.TextMap;
import io.opentracing.propagation.TextMapAdapter;
import io.opentracing.propagation.TextMapExtract;
import io.opentracing.propagation.TextMapInject;
import io.opentracing.propagation.TextMapInjectAdapter;

/**
 * Since the TracingStartup is an EJB bean, there shouldn't be any final or static
 * methods as a part of it (see WFLYEJB0131 for more information). This class therefore
 * has all the "helper" methods.
 * 
 * @author Miloslav Zezulka (mzezulka@redhat.com)
 *
 */
public class TracingHelper {

    private static final String TRACER_CONFIG_LOCATION = "tracer_config.properties";
    private static final TextMapCodec TEXT_MAP_CODEC= new TextMapCodec.Builder().build();
    
    static Tracer getJaegerTracer(Properties config) {
        SamplerConfiguration samplerConfig = new SamplerConfiguration().withType("const").withParam(1);
        SenderConfiguration senderConfig = new SenderConfiguration()
                .withAgentHost(config.getProperty("jaeger.reporter_host"))
                .withAgentPort(Integer.decode(config.getProperty("jaeger.reporter_port")));
        ReporterConfiguration reporterConfig = new ReporterConfiguration()
                .withLogSpans(true)
                .withFlushInterval(1000)
                .withMaxQueueSize(10000)
                .withSender(senderConfig);
        Builder bldr = new Configuration(System.getProperty("user.dir") + ":" + config.getProperty("tracer.component_name"))
                .withSampler(samplerConfig)
                .withReporter(reporterConfig)
                .getTracerBuilder();
        bldr.registerInjector(Format.Builtin.TEXT_MAP_INJECT, new TextMapInjector());
        bldr.registerExtractor(Format.Builtin.TEXT_MAP_EXTRACT, new TextMapExtractor());
        return bldr.build();
    }
    
    private static class TextMapExtractor implements Extractor<TextMapExtract> {

        @Override
        public JaegerSpanContext extract(TextMapExtract carrier) {
             TextMap tm = new TextMap() {
                
                @Override
                public Iterator<Entry<String, String>> iterator() {
                    return carrier.iterator();
                }
                
                @Override
                public void put(String key, String value) {
                    throw new IllegalArgumentException("We do not expect inject related methods to be called.");
                }
            };
            return TEXT_MAP_CODEC.extract(tm);
        }
        
    }

    private static class TextMapInjector implements Injector<TextMapInject>  {
        
        @Override
        public void inject(JaegerSpanContext spanContext, TextMapInject carrier) {
            TextMap tm = new TextMap() {
                
                @Override
                public Iterator<Entry<String, String>> iterator() {
                    throw new IllegalArgumentException("We do not expect extract related methods to be called.");
                }
                
                @Override
                public void put(String key, String value) {
                    carrier.put(key, value);
                }
            };
            TEXT_MAP_CODEC.inject(spanContext, tm);
        }
    }

    static Properties loadConfig() {
        try (InputStream fs = TracingStartup.class.getClassLoader().getResourceAsStream(TRACER_CONFIG_LOCATION)) {
            Properties config = new Properties();
            config.load(fs);
            return config;
        } catch (IOException ex) {
            // unrecoverable exception
            throw new RuntimeException(ex);
        }
    }
}
