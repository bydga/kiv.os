rmdir /s /q bin
md bin
cd src
for /r %%a in (/*.java) do ( javac -d ../bin "%%a" )
cd ..