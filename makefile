all: ; cd client && $(MAKE) all ; cd ../server && $(MAKE) all

Chat.jar: ; cd client && $(MAKE) LightBikes.jar ; cd ../server && $(MAKE) LightBikes.jar
	
%.class: ; cd client && $(MAKE) %.class ; cd ../server && $(MAKE) %.class
	
clean: ; cd client && $(MAKE) clean ; cd ../server && $(MAKE) clean
	
proper: ; cd client && $(MAKE) proper ; cd ../server && $(MAKE) proper
