package com.management.demo.repository;

import com.management.demo.model.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class MemberRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // RowMapper to convert database rows to Member objects
    private final RowMapper<Member> memberRowMapper = (rs, rowNum) -> {
        Member member = new Member();
        member.setId(rs.getLong("id"));
        member.setMemberId(rs.getString("member_id"));
        member.setFirstName(rs.getString("first_name"));
        member.setLastName(rs.getString("last_name"));
        member.setEmail(rs.getString("email"));
        member.setPhone(rs.getString("phone"));
        member.setAddress(rs.getString("address"));

        // Handle date fields
        if (rs.getDate("membership_date") != null) {
            member.setMembershipDate(rs.getDate("membership_date").toLocalDate());
        }
        member.setIsActive(rs.getBoolean("is_active"));

        // Handle timestamps
        if (rs.getTimestamp("created_at") != null) {
            member.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        }
        if (rs.getTimestamp("updated_at") != null) {
            member.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        }

        return member;
    };

    // Find all members
    public List<Member> findAll() {
        String sql = "SELECT * FROM members ORDER BY first_name, last_name";
        return jdbcTemplate.query(sql, memberRowMapper);
    }

    // Find member by ID
    public Optional<Member> findById(Long id) {
        String sql = "SELECT * FROM members WHERE id = ?";
        List<Member> members = jdbcTemplate.query(sql, memberRowMapper, id);
        return members.isEmpty() ? Optional.empty() : Optional.of(members.get(0));
    }

    // Find member by member ID
    public Optional<Member> findByMemberId(String memberId) {
        String sql = "SELECT * FROM members WHERE member_id = ?";
        List<Member> members = jdbcTemplate.query(sql, memberRowMapper, memberId);
        return members.isEmpty() ? Optional.empty() : Optional.of(members.get(0));
    }

    // Find member by email
    public Optional<Member> findByEmail(String email) {
        String sql = "SELECT * FROM members WHERE email = ?";
        List<Member> members = jdbcTemplate.query(sql, memberRowMapper, email);
        return members.isEmpty() ? Optional.empty() : Optional.of(members.get(0));
    }

    // Save member (insert or update)
    public Member save(Member member) {
        if (member.getId() == null) {
            return insert(member);
        } else {
            return update(member);
        }
    }

    // Insert new member using PostgreSQL RETURNING clause
    private Member insert(Member member) {
        String sql = "INSERT INTO members (member_id, first_name, last_name, email, phone, address, membership_date, is_active, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";

        LocalDateTime now = LocalDateTime.now();
        LocalDate membershipDate = member.getMembershipDate() != null ?
                member.getMembershipDate() : LocalDate.now();

        Integer generatedId = jdbcTemplate.queryForObject(sql, Integer.class,
                member.getMemberId(),
                member.getFirstName(),
                member.getLastName(),
                member.getEmail(),
                member.getPhone(),
                member.getAddress(),
                membershipDate,
                member.getIsActive(),
                now,
                now
        );

        member.setId(generatedId.longValue());
        member.setMembershipDate(membershipDate);
        member.setCreatedAt(now);
        member.setUpdatedAt(now);

        return member;
    }

    // Update existing member
    private Member update(Member member) {
        String sql = "UPDATE members SET member_id = ?, first_name = ?, last_name = ?, email = ?, phone = ?, address = ?, membership_date = ?, is_active = ?, updated_at = ? WHERE id = ?";

        LocalDateTime now = LocalDateTime.now();
        jdbcTemplate.update(sql,
                member.getMemberId(),
                member.getFirstName(),
                member.getLastName(),
                member.getEmail(),
                member.getPhone(),
                member.getAddress(),
                member.getMembershipDate(),
                member.getIsActive(),
                now,
                member.getId()
        );

        member.setUpdatedAt(now);
        return member;
    }

    // Delete member
    public void deleteById(Long id) {
        String sql = "DELETE FROM members WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    // Search members by name or email
    public List<Member> searchMembers(String searchTerm) {
        String sql = "SELECT * FROM members WHERE LOWER(first_name) LIKE LOWER(?) OR LOWER(last_name) LIKE LOWER(?) OR LOWER(email) LIKE LOWER(?) ORDER BY first_name, last_name";
        String searchPattern = "%" + searchTerm + "%";
        return jdbcTemplate.query(sql, memberRowMapper, searchPattern, searchPattern, searchPattern);
    }
}
