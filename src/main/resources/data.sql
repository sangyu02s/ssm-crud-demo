-- Demo rows loaded when the H2 database starts.
insert into books (title, author, price, created_at, updated_at)
values
    ('Spring MVC Guide', 'Spring Team', 59.90, current_timestamp, current_timestamp),
    ('MyBatis Essentials', 'Example Author', 49.50, current_timestamp, current_timestamp);
