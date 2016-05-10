FROM node:argon
RUN mkdir -p /usr/src/siqapp
WORKDIR /usr/src/siqapp

COPY package.json /usr/src/siqapp
RUN npm install
COPY . /usr/src/siqapp

EXPOSE 8080
CMD ["node","siq.js"]