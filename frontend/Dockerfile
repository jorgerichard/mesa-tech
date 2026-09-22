FROM node:22-alpine AS build
WORKDIR /app
ARG REACT_APP_API_BASE_URL=http://localhost:8080
ENV REACT_APP_API_BASE_URL=$REACT_APP_API_BASE_URL
COPY package.json .
RUN npm install
COPY public public
COPY src src
RUN npm run build
FROM nginx:1.27-alpine
COPY --from=build /app/build /usr/share/nginx/html
EXPOSE 80
