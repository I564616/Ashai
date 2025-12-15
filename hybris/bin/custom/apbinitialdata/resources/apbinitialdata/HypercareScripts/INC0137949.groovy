codeGenerator = spring.getBean("codeGenerator")
codeGenerator.generate()
codeGenerator.setStart("00080000")
codeGenerator.reset()