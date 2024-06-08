# Java Network Programming Practice

This repository contains examples and exercises related to Java's NIO (Non-blocking I/O). It covers the three main components of NIO, network programming, zero-copy techniques, I/O models, and the Netty framework.

## Features

- **NIO (Non-blocking I/O)**: Advanced I/O operations for high scalability.
- **Zero-Copy**: Efficient data transfer techniques to minimize CPU usage.
- **I/O Models**: Implementation of different I/O models including synchronous and asynchronous models.
- **Netty Framework**: Exercises and examples using the Netty framework for building network applications.

## NIO Components

Java NIO introduces three primary components that are key to its non-blocking I/O capabilities:

1. **Channels**: Channels represent open connections to entities such as hardware devices, files, or sockets. They are similar to streams but provide more versatile functionality and can be both readable and writable.

2. **Buffers**: Buffers are containers for data that is being transferred between a channel and an application. Unlike streams, buffers can be read from and written to, making them essential for handling data in NIO.

3. **Selectors**: Selectors allow a single thread to manage multiple channels, enabling non-blocking I/O operations. A selector can monitor multiple channels for events (such as data arrival or connection requests), facilitating efficient resource utilization.

## Example: MultiThreadServer

The `MultiThreadServer` example demonstrates how to use these NIO components to create a scalable, multi-threaded server that can handle multiple client connections efficiently.
## Getting Started

1. **Clone the repository:**

   ```sh
   git clone https://github.com/ShawnJeffersonWang/Netty.git
   cd Netty
   ```

2. **Build the project:**
   Ensure you have Java and Maven installed. Then run:

   ```sh
   mvn clean install
   ```

3. **Run the examples:**
   Navigate to the example you want to run and execute it using your preferred IDE or command line.

## Dependencies

This project relies on the following dependencies:

- **SLF4J:** For logging.
- **Netty:** For advanced network programming examples.

Ensure these dependencies are properly configured in your project setup before running the examples.

## Usage

1. **Explore the examples:** Browse through the different packages to find examples related to BIO, NIO, zero-copy, and Netty.
2. **Run and modify the code:** Execute the examples and experiment with the code to deepen your understanding.
3. **Refer to the documentation:** Use the comments and documentation within the code to guide your learning process.

## Contributing

Contributions are welcome! If you find any issues or have suggestions for improvements, please submit a pull request or open an issue on this repository.

When contributing, please adhere to the existing code style and follow the established guidelines.

## License

This project is licensed under the [MIT License](LICENSE). You are free to modify and distribute the code as per the license terms.
