package com.management.demo.service;

import com.management.demo.model.Member;
import com.management.demo.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class MemberService {

    @Autowired
    private MemberRepository memberRepository;

    // Get all members
    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    // Get member by ID
    public Optional<Member> getMemberById(Long id) {
        return memberRepository.findById(id);
    }

    // Get member by member ID
    public Optional<Member> getMemberByMemberId(String memberId) {
        return memberRepository.findByMemberId(memberId);
    }

    // Get member by email
    public Optional<Member> getMemberByEmail(String email) {
        return memberRepository.findByEmail(email);
    }

    // Add new member
    public Member addMember(Member member) {
        // Check if member ID already exists
        if (memberRepository.findByMemberId(member.getMemberId()).isPresent()) {
            throw new RuntimeException("Member with ID " + member.getMemberId() + " already exists");
        }

        // Check if email already exists
        if (memberRepository.findByEmail(member.getEmail()).isPresent()) {
            throw new RuntimeException("Member with email " + member.getEmail() + " already exists");
        }

        // Set default values if not provided
        if (member.getMembershipDate() == null) {
            member.setMembershipDate(LocalDate.now());
        }
        if (member.getIsActive() == null) {
            member.setIsActive(true);
        }

        return memberRepository.save(member);
    }

    // Update member
    public Member updateMember(Long id, Member memberDetails) {
        Optional<Member> existingMember = memberRepository.findById(id);
        if (existingMember.isEmpty()) {
            throw new RuntimeException("Member not found with id: " + id);
        }

        Member member = existingMember.get();

        // Check if new member ID conflicts with existing member (excluding current member)
        Optional<Member> memberWithSameId = memberRepository.findByMemberId(memberDetails.getMemberId());
        if (memberWithSameId.isPresent() && !memberWithSameId.get().getId().equals(id)) {
            throw new RuntimeException("Member ID " + memberDetails.getMemberId() + " is already taken");
        }

        // Check if new email conflicts with existing member (excluding current member)
        Optional<Member> memberWithSameEmail = memberRepository.findByEmail(memberDetails.getEmail());
        if (memberWithSameEmail.isPresent() && !memberWithSameEmail.get().getId().equals(id)) {
            throw new RuntimeException("Email " + memberDetails.getEmail() + " is already taken");
        }

        // Update member details
        member.setMemberId(memberDetails.getMemberId());
        member.setFirstName(memberDetails.getFirstName());
        member.setLastName(memberDetails.getLastName());
        member.setEmail(memberDetails.getEmail());
        member.setPhone(memberDetails.getPhone());
        member.setAddress(memberDetails.getAddress());
        member.setMembershipDate(memberDetails.getMembershipDate());
        member.setIsActive(memberDetails.getIsActive());

        return memberRepository.save(member);
    }

    // Delete member
    public void deleteMember(Long id) {
        if (memberRepository.findById(id).isEmpty()) {
            throw new RuntimeException("Member not found with id: " + id);
        }
        memberRepository.deleteById(id);
    }

    // Search members
    public List<Member> searchMembers(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllMembers();
        }
        return memberRepository.searchMembers(searchTerm.trim());
    }

    // Deactivate member (soft delete)
    public Member deactivateMember(Long id) {
        Optional<Member> existingMember = memberRepository.findById(id);
        if (existingMember.isEmpty()) {
            throw new RuntimeException("Member not found with id: " + id);
        }

        Member member = existingMember.get();
        member.setIsActive(false);
        return memberRepository.save(member);
    }

    // Activate member
    public Member activateMember(Long id) {
        Optional<Member> existingMember = memberRepository.findById(id);
        if (existingMember.isEmpty()) {
            throw new RuntimeException("Member not found with id: " + id);
        }

        Member member = existingMember.get();
        member.setIsActive(true);
        return memberRepository.save(member);
    }

    // Get active members only
    public List<Member> getActiveMembers() {
        return getAllMembers().stream()
                .filter(Member::getIsActive)
                .toList();
    }
}
