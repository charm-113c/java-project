build:
	@echo "Compiling"
	@javac -d bin src/**/*.java

entrypoint = Controller.Main

run: build
	@cd bin && java $(entrypoint)
