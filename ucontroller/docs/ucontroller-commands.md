# uController Command Definitions

## Configuration Update

Format: C:n

| Placeholder | Meaning                                       |
| ------------| ----------------------------------------------|
| n           | New number of LEDs the uController is driving |

## LED Update

Format: U:n,r,g,b,br

| Placeholder | Meaning             |
| ------------| --------------------|
| n           | LED # to update     |
| r           | Red channel value   |
| g           | Green channel value |
| b           | Blue channel value  |
| br          | Brightness          |

## Power Update

Format: P:n

| Placeholder | Meaning             |
| ------------| --------------------|
| n           | 0 for off, 1 for on |

## Status Request

Format: R
Response Format: P:n,T:c

| Placeholder | Meaning                          |
| ------------| ---------------------------------|
| n           | power state, 0 for off, 1 for on |
| c           | temperature in C                 |
