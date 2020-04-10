package io.narayana.txdemo.tracing;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import io.jaegertracing.Configuration;
import io.jaegertracing.Configuration.ReporterConfiguration;
import io.jaegertracing.Configuration.SamplerConfiguration;
import io.jaegertracing.Configuration.SenderConfiguration;
import io.jaegertracing.internal.JaegerTracer.Builder;
import io.jaegertracing.internal.propagation.TextMapCodec;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;

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
        Builder bldr = new Configuration(serviceName(config))
                .withSampler(samplerConfig)
                .withReporter(reporterConfig)
                .getTracerBuilder();
        bldr.registerInjector(Format.Builtin.TEXT_MAP, TEXT_MAP_CODEC);
        bldr.registerExtractor(Format.Builtin.TEXT_MAP, TEXT_MAP_CODEC);
        return bldr.build();
    }
    
    private static String serviceName(Properties config) {
        String[] wflyPath = System.getProperty("user.dir").split("/");
        return wflyPath[wflyPath.length-1] + ":" + config.getProperty("tracer.component_name");
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
