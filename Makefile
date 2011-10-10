NAME=graylog2-server
PREFIX=/usr
DESTDIR=

SERVER_W_DEP=target/graylog2-server-0.9.6-SNAPSHOT-jar-with-dependencies.jar
SERVER=target/graylog2-server-0.9.6-SNAPSHOT.jar
INITD=contrib/distro/generic/graylog2-server.init.d
CONF=misc/graylog2.conf

all: $(SERVER) $(SERVER_W_DEP) test

$(SERVER) $(SERVER_W_DEP):
	mvn assembly:assembly

test:
	mvn test

clean:
	mvn clean

install: $(SERVER_W_DEP) $(INITD)
	install -m 755 -d $(DESTDIR)$(PREFIX)/share/$(NAME)
	install -m 0644 $(SERVER_W_DEP) $(DESTDIR)$(PREFIX)/share/$(NAME)/graylog2-server.jar
	install -m 755 -d $(DESTDIR)/etc/init.d
	install -m 0755 $(INITD) $(DESTDIR)/etc/init.d/graylog2-server
	install -m 0600 $(CONF) $(DESTDIR)/etc/graylog2.conf
