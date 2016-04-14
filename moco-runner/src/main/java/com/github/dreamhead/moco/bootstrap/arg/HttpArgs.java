package com.github.dreamhead.moco.bootstrap.arg;

import com.github.dreamhead.moco.bootstrap.ServerType;

public class HttpArgs extends StartArgs {
    protected HttpArgs(final Integer port, final Integer shutdownPort,
                       final String configurationFile, final String globalSettings,
                       final String env) {
        super(ServerType.HTTP, port, shutdownPort, configurationFile, globalSettings, env, null);
    }

    public static Builder httpArgs() {
        return new Builder();
    }

    public static class Builder {
        private Integer port;
        private Integer shutdownPort;
        private String configurationFile;
        private String settings;
        private String env;

        public Builder withPort(final Integer port) {
            this.port = port;
            return this;
        }

        public Builder withShutdownPort(final Integer shutdownPort) {
            this.shutdownPort = shutdownPort;
            return this;
        }

        public Builder withConfigurationFile(final String configurationFile) {
            this.configurationFile = configurationFile;
            return this;
        }

        public Builder withSettings(final String settings) {
            this.settings = settings;
            return this;
        }

        public Builder withEnv(final String env) {
            this.env = env;
            return this;
        }

        public HttpArgs build() {
            return new HttpArgs(port, shutdownPort, configurationFile, settings, env);
        }
    }
}
